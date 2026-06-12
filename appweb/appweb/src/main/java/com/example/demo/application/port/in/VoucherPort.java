package com.example.demo.application.port.in;

import com.example.demo.domain.model.Voucher;

import java.util.List;

public interface VoucherPort {

    Voucher registerVoucher(Voucher voucher);

    Voucher reconcileVoucher(Long voucherId);

    List<Voucher> findDuplicates(String operationNumber, String bank);

    List<Voucher> listAllVouchers();
}
