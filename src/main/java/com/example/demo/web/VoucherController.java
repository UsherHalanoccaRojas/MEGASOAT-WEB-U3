package com.example.demo.web;

import com.example.demo.application.port.in.VoucherPort;
import com.example.demo.domain.model.Voucher;
import com.example.demo.web.dto.BusinessDTOs.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {

    private final VoucherPort voucherPort;

    public VoucherController(VoucherPort voucherPort) {
        this.voucherPort = voucherPort;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','COMERCIAL')")
    public ResponseEntity<Voucher> register(@RequestBody VoucherRequestDTO request) {
        Voucher voucher = new Voucher();
        voucher.setOperationNumber(request.operationNumber());
        voucher.setBank(request.bank());
        voucher.setAmount(request.amount());
        voucher.setType(request.type());
        voucher.setOperationDate(request.operationDate() != null ? request.operationDate() : LocalDate.now());
        voucher.setOperationTime(request.operationTime());
        voucher.setRegisteredBy(request.registeredBy());
        return ResponseEntity.ok(voucherPort.registerVoucher(voucher));
    }

    @PostMapping("/{id}/reconcile")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','COMERCIAL')")
    public ResponseEntity<Voucher> reconcile(@PathVariable Long id) {
        return ResponseEntity.ok(voucherPort.reconcileVoucher(id));
    }

    @GetMapping("/duplicates")
    public ResponseEntity<List<Voucher>> findDuplicates(@RequestParam String operationNumber, @RequestParam String bank) {
        return ResponseEntity.ok(voucherPort.findDuplicates(operationNumber, bank));
    }

    @GetMapping
    public ResponseEntity<List<Voucher>> listAll() {
        return ResponseEntity.ok(voucherPort.listAllVouchers());
    }
}
