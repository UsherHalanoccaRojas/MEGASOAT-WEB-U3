package com.example.estadisticas;

import jakarta.persistence.*;

@Entity
@Table(name = "puntos_venta")
public class PointOfSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String name;

    @Column(name = "ciudad", nullable = false)
    private String city;

    @Column(nullable = false)
    private boolean activo = true;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public boolean isActivo() { return activo; }
}
