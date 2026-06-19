package com.example.demo.infrastructure.monitoring;

import java.time.Instant;
import java.util.Set;

public class MonitoringActivity {
    private final long id;
    private final Instant timestamp;
    private final String type;
    private final String actor;
    private final Set<String> roles;
    private final String method;
    private final String path;
    private final String ip;
    private final String userAgent;
    private final int status;
    private final long durationMs;
    private final String details;

    public MonitoringActivity(long id,
                              Instant timestamp,
                              String type,
                              String actor,
                              Set<String> roles,
                              String method,
                              String path,
                              String ip,
                              String userAgent,
                              int status,
                              long durationMs,
                              String details) {
        this.id = id;
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

    public long getId() { return id; }
    public Instant getTimestamp() { return timestamp; }
    public String getType() { return type; }
    public String getActor() { return actor; }
    public Set<String> getRoles() { return roles; }
    public String getMethod() { return method; }
    public String getPath() { return path; }
    public String getIp() { return ip; }
    public String getUserAgent() { return userAgent; }
    public int getStatus() { return status; }
    public long getDurationMs() { return durationMs; }
    public String getDetails() { return details; }
}