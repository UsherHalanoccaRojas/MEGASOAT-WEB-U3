package com.example.demo.domain.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false)
    private String type;

    private String actor;

    @Column(columnDefinition = "TEXT")
    private String roles;

    private String method;

    @Column(columnDefinition = "TEXT")
    private String path;

    private String ip;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    private int status;

    @Column(name = "duration_ms")
    private long durationMs;

    @Column(columnDefinition = "TEXT")
    private String details;

    public AuditLog() {}

    public AuditLog(Instant timestamp, String type, String actor, String roles, String method, String path, String ip, String userAgent, int status, long durationMs, String details) {
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public long getDurationMs() { return durationMs; }
    public void setDurationMs(long durationMs) { this.durationMs = durationMs; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
