package com.example.demo.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class BusinessDTOs {
    public record PointOfSaleRequestDTO(String name, String city, String responsible) {}
    
    public record RoleAssignmentRequestDTO(String email, String role) {}
    
    public record UserDTO(String nombre, String email, String rol, String estado, String avatarUrl) {
        public UserDTO(String nombre, String email, String rol, boolean activo, String avatarUrl) {
            this(nombre, email, rol, activo ? "Activo" : "Inactivo", avatarUrl);
        }
    }
    
    public record VoucherRequestDTO(
            @NotBlank(message = "El número de operación es obligatorio") String operationNumber,
            @NotBlank(message = "El banco es obligatorio") String bank,
            @NotNull(message = "El monto es obligatorio") @Positive(message = "El monto debe ser positivo") BigDecimal amount,
            @NotBlank(message = "El tipo de operación es obligatorio") String type,
            @NotNull(message = "La fecha de operación es obligatoria") LocalDate operationDate,
            LocalTime operationTime,
            @NotNull(message = "El usuario que registra es obligatorio") Long registeredBy
    ) {}
}
