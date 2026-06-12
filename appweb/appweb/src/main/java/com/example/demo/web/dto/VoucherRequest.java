package com.example.demo.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class VoucherRequest {
    private String operationNumber;
    private String bank;
    private BigDecimal amount;
    private String type;
    private LocalDate operationDate;
    private LocalTime operationTime;
    private Long registeredBy;

    public String getOperationNumber() {
        return operationNumber;
    }

    public void setOperationNumber(String operationNumber) {
        this.operationNumber = operationNumber;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(LocalDate operationDate) {
        this.operationDate = operationDate;
    }

    public LocalTime getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(LocalTime operationTime) {
        this.operationTime = operationTime;
    }

    public Long getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(Long registeredBy) {
        this.registeredBy = registeredBy;
    }
}
