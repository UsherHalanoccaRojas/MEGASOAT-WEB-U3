package com.example.demo.application.service;

import com.example.demo.application.exception.DuplicateResourceException;
import com.example.demo.application.exception.ResourceNotFoundException;
import com.example.demo.application.port.in.NotificationPort;
import com.example.demo.application.port.in.VoucherPort;
import com.example.demo.domain.model.Voucher;
import com.example.demo.infrastructure.persistence.VoucherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VoucherReconciliationService implements VoucherPort {

    private final VoucherRepository voucherRepository;
    private final NotificationPort notificationPort;

    public VoucherReconciliationService(VoucherRepository voucherRepository,
                                        NotificationPort notificationPort) {
        this.voucherRepository = voucherRepository;
        this.notificationPort = notificationPort;
    }

    @Override
    public Voucher registerVoucher(Voucher voucher) {
        List<Voucher> duplicates = voucherRepository.findByOperationNumberAndBank(voucher.getOperationNumber(), voucher.getBank());
        if (!duplicates.isEmpty()) {
            voucher.setDuplicate(true);
            voucher.setFraudSuspected(true);
            notificationPort.sendTelegramAlert("Duplicado detectado para voucher " + voucher.getOperationNumber());
            throw new DuplicateResourceException("Voucher duplicado detectado: " + voucher.getOperationNumber());
        }
        voucher.setValidated(false);
        voucher.setRegisteredAt(voucher.getRegisteredAt() == null ? java.time.LocalDateTime.now() : voucher.getRegisteredAt());
        return voucherRepository.save(voucher);
    }

    @Override
    public Voucher reconcileVoucher(Long voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher no encontrado: " + voucherId));
        voucher.setValidated(true);
        return voucherRepository.save(voucher);
    }

    @Override
    public List<Voucher> findDuplicates(String code, String customerDocument) {
        return voucherRepository.findByOperationNumberAndBank(code, customerDocument);
    }

    @Override
    public List<Voucher> listAllVouchers() {
        return voucherRepository.findAll();
    }
}
