package com.example.demo.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "puntos_venta")
public class PointOfSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(name = "nombre", nullable = false)
    private String name;

    @Column(name = "ciudad", nullable = false)
    private String city;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "telegram_chat_id")
    private String telegramChatId;

    @Column(name = "contacto_nombre")
    private String responsible;

    @Column(name = "contacto_telefono")
    private String contactoTelefono;

    @Column(name = "email")
    private String email;

    @Column(name = "comision_personalizada")
    private Double comisionPersonalizada;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Transient
    private int salesCount;

    @Transient
    private double performanceScore;

    @Transient
    private double delinquencyRate;

    @Transient
    private LocalDateTime lastUpdated = LocalDateTime.now();

    public PointOfSale() {
    }

    public PointOfSale(String name, String city) {
        this.name = name;
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isActive() {
        return activo;
    }

    public void setActive(boolean active) {
        this.activo = active;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public int getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(int salesCount) {
        this.salesCount = salesCount;
    }

    public double getPerformanceScore() {
        return performanceScore;
    }

    public void setPerformanceScore(double performanceScore) {
        this.performanceScore = performanceScore;
    }

    public double getDelinquencyRate() {
        return delinquencyRate;
    }

    public void setDelinquencyRate(double delinquencyRate) {
        this.delinquencyRate = delinquencyRate;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
