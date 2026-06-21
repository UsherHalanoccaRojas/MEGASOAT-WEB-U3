package com.example.monitoring;

import jakarta.persistence.*;
import java.time.Instant;

// Entidad JPA que mapea la tabla "audit_logs" en la base de datos.
// Cada fila representa un evento registrado en el sistema.
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant timestamp;  // Cuándo ocurrió el evento
    private String type;        // Tipo: REQUEST, LOGIN, UI_EVENT, etc.
    private String actor;       // Email del usuario que generó el evento
    private String roles;       // Roles del usuario (ej: "ROLE_SUPERADMIN")
    private String method;      // Método HTTP (GET, POST, etc.)

    @Column(columnDefinition = "TEXT")
    private String path;        // Ruta accedida (ej: /api/dashboard/stats)

    private String ip;          // IP del cliente

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;   // Navegador/cliente del usuario

    private int status;         // Código HTTP de respuesta (200, 401, etc.)

    @Column(name = "duration_ms")
    private long durationMs;    // Tiempo que tardó la petición en ms

    @Column(columnDefinition = "TEXT")
    private String details;     // Descripción legible del evento

    // Constructor vacío requerido por JPA
    public AuditLog() {}

    // Constructor completo para crear un registro desde código
    public AuditLog(Instant timestamp, String type, String actor, String roles,
                    String method, String path, String ip, String userAgent,
                    int status, long durationMs, String details) {
        this.timestamp = timestamp;
        this.type = type;
        this.actor = actor;
        this.roles = roles;
        this.method = method;
        this.path = path;
        this.ip = ip;
        this.userAgent = userAgent;
        this.status = status;
        this.durationMs = durationMs;
        this.details = details;
    }

    // Getters y setters
    public Long getId() { return id; }
    public Instant getTimestamp() { return timestamp; }
    public String getType() { return type; }
    public String getActor() { return actor; }
    public String getRoles() { return roles; }
    public String getMethod() { return method; }
    public String getPath() { return path; }
    public String getIp() { return ip; }
    public String getUserAgent() { return userAgent; }
    public int getStatus() { return status; }
    public long getDurationMs() { return durationMs; }
    public String getDetails() { return details; }
}
