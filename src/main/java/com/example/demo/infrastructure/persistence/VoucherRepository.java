package com.example.demo.infrastructure.persistence;

import com.example.demo.domain.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByOperationNumber(String operationNumber);
    List<Voucher> findByOperationNumberAndBank(String operationNumber, String bank);
}
