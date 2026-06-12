package com.example.demo.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ventas")
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pv_id", nullable = false)
    private Long pvId;

    @Column(name = "numero_poliza", unique = true)
    private String policyNumber;

    @Column(name = "placa", nullable = false)
    private String vehiclePlate;

    @Column(name = "aseguradora", nullable = false)
    private String insurer;

    @Column(name = "tipo_vehiculo", nullable = false)
    private String vehicleType;

    @Column(name = "canal", nullable = false)
    private String channel;

    @Column(name = "prima", nullable = false)
    private BigDecimal premium;

    @Column(name = "comision_pv")
    private BigDecimal commissionPv;

    @Column(name = "comision_empresa")
    private BigDecimal commissionCompany;

    @Column(name = "estado", nullable = false)
    private String status;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(name = "fecha_emision")
    private LocalDateTime issueDate;

    @Column(name = "fecha_vencimiento")
    private LocalDate dueDate;

    @Column(name = "fecha_pago")
    private LocalDateTime paymentDate;

    @Column(name = "observaciones")
    private String observations;

    @Column(name = "emitido_por")
    private Long issuedBy;

    public Policy() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPvId() {
        return pvId;
    }

    public void setPvId(Long pvId) {
        this.pvId = pvId;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }

    public String getInsurer() {
        return insurer;
    }

    public void setInsurer(String insurer) {
        this.insurer = insurer;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public BigDecimal getCommissionPv() {
        return commissionPv;
    }

    public void setCommissionPv(BigDecimal commissionPv) {
        this.commissionPv = commissionPv;
    }

    public BigDecimal getCommissionCompany() {
        return commissionCompany;
    }

    public void setCommissionCompany(BigDecimal commissionCompany) {
        this.commissionCompany = commissionCompany;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Long getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(Long issuedBy) {
        this.issuedBy = issuedBy;
    }
}
