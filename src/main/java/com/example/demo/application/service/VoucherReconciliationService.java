package com.example.demo.application.service;

import com.example.demo.application.exception.DuplicateResourceException;
import com.example.demo.application.exception.ResourceNotFoundException;
import com.example.demo.application.port.in.NotificationPort;
import com.example.demo.application.port.in.VoucherPort;
import com.example.demo.domain.event.VoucherDuplicatedEvent;
import com.example.demo.domain.model.Voucher;
import com.example.demo.infrastructure.persistence.VoucherRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.context.ApplicationEventPublisher;

@Service
@Transactional
public class VoucherReconciliationService implements VoucherPort {

    private final VoucherRepository voucherRepository;
    private final ApplicationEventPublisher eventPublisher;

    public VoucherReconciliationService(VoucherRepository voucherRepository,
                                        ApplicationEventPublisher eventPublisher) {
        this.voucherRepository = voucherRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Voucher registerVoucher(Voucher voucher) {
        List<Voucher> duplicates = voucherRepository.findByOperationNumberAndBank(voucher.getOperationNumber(), voucher.getBank());
        if (!duplicates.isEmpty()) {
            voucher.setDuplicate(true);
            voucher.setFraudSuspected(true);
            eventPublisher.publishEvent(new VoucherDuplicatedEvent(voucher.getOperationNumber()));
            throw new DuplicateResourceException("Voucher duplicado detectado: " + voucher.getOperationNumber());
        }

        voucher.setValidated(false);
        voucher.setRegisteredAt(voucher.getRegisteredAt() == null ? LocalDateTime.now() : voucher.getRegisteredAt());
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
