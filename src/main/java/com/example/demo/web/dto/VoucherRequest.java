package com.example.demo.web.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class VoucherRequest {

    @NotBlank(message = "El número de operación es obligatorio")
    private String operationNumber;

    @NotBlank(message = "El banco es obligatorio")
    private String bank;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal amount;

    @NotBlank(message = "El tipo de operación es obligatorio")
    private String type;

    @NotNull(message = "La fecha de operación es obligatoria")
    private LocalDate operationDate;

    private LocalTime operationTime;

    @NotNull(message = "El usuario que registra es obligatorio")
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
