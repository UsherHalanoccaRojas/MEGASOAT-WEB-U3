package com.example.estadisticas;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activo", nullable = false)
    private boolean active = true;

    @Column(name = "rol")
    private String rol;

    public Long getId() { return id; }
    public boolean isActive() { return active; }
    public String getRol() { return rol; }
}
