package com.example.demo.web;

import com.example.demo.domain.model.Voucher;
import com.example.demo.infrastructure.persistence.VoucherRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/vouchers")
public class AdminVoucherController {

    private final VoucherRepository voucherRepository;

    public AdminVoucherController(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','COMERCIAL')")
    public ResponseEntity<List<Voucher>> listAll() {
        return ResponseEntity.ok(voucherRepository.findAll());
    }
}