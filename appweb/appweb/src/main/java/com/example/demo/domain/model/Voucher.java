package com.example.demo.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "vouchers")
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "operation_number", nullable = false, unique = true)
    private String operationNumber;

    @Column(name = "banco", nullable = false)
    private String bank;

    @Column(name = "monto", nullable = false)
    private BigDecimal amount;

    @Column(name = "tipo", nullable = false)
    private String type;

    @Column(name = "fecha_operacion", nullable = false)
    private LocalDate operationDate;

    @Column(name = "hora_operacion")
    private LocalTime operationTime;

    @Column(name = "observaciones")
    private String observations;

    @Column(name = "registrado_por", nullable = false)
    private Long registeredBy;

    @Column(name = "fecha_registro")
    private LocalDateTime registeredAt;

    @Column(name = "validado")
    private boolean validated;

    @Column(name = "validado_por")
    private Long validatedBy;

    @Column(name = "fecha_validacion")
    private LocalDateTime validatedAt;

    private boolean duplicate;
    private boolean reconciled;
    private boolean fraudSuspected;

    public Voucher() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Long getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(Long registeredBy) {
        this.registeredBy = registeredBy;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public Long getValidatedBy() {
        return validatedBy;
    }

    public void setValidatedBy(Long validatedBy) {
        this.validatedBy = validatedBy;
    }

    public LocalDateTime getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(LocalDateTime validatedAt) {
        this.validatedAt = validatedAt;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public boolean isReconciled() {
        return reconciled;
    }

    public void setReconciled(boolean reconciled) {
        this.reconciled = reconciled;
    }

    public boolean isFraudSuspected() {
        return fraudSuspected;
    }

    public void setFraudSuspected(boolean fraudSuspected) {
        this.fraudSuspected = fraudSuspected;
    }
}
