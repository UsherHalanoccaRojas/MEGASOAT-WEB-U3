package com.example.demo.web.dto;

public class UserDTO {
    private String nombre;
    private String email;
    private String rol;
    private String estado;
    private String avatarUrl;

    public UserDTO(String nombre, String email, String rol, boolean activo, String avatarUrl) {
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.estado = activo ? "Activo" : "Inactivo";
        this.avatarUrl = avatarUrl;
    }

    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getRol() { return rol; }
    public String getEstado() { return estado; }
    public String getAvatarUrl() { return avatarUrl; }
}
