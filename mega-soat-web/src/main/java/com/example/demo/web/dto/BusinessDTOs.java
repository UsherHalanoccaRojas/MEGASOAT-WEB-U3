package com.example.demo.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class BusinessDTOs {
    public record PointOfSaleRequestDTO(String name, String city, String responsible, String email, String telefono, String direccion) {}
    
    public record RoleAssignmentRequestDTO(String email, String role) {}
    
    public record UserDTO(String nombre, String email, String rol, String estado, String avatarUrl) {
        public UserDTO(String nombre, String email, String rol, boolean activo, String avatarUrl) {
            this(nombre, email, rol, activo ? "Activo" : "Inactivo", avatarUrl);
        }
    }
    
}
