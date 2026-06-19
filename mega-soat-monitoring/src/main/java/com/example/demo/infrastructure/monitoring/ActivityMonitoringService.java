package com.example.demo.infrastructure.monitoring;

import com.example.demo.domain.model.AuditLog;
import com.example.demo.infrastructure.persistence.AuditLogRepository;
import com.example.demo.infrastructure.service.WebSocketPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ActivityMonitoringService {

    private static final String TOPIC = "/topic/admin/monitoring";

    private final WebSocketPublisher webSocketPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuditLogRepository auditLogRepository;

    public ActivityMonitoringService(ObjectProvider<WebSocketPublisher> webSocketPublisherProvider, AuditLogRepository auditLogRepository) {
        this.webSocketPublisher = webSocketPublisherProvider.getIfAvailable();
        this.auditLogRepository = auditLogRepository;
    }

    public void recordAuthenticationEvent(String type,
                                          String actor,
                                          Set<String> roles,
                                          HttpServletRequest request,
                                          int status,
                                          String details) {
        record(type, actor, roles, request, status, details, 0L);
    }

    public void recordAction(String type,
                             String actor,
                             Set<String> roles,
                             HttpServletRequest request,
                             int status,
                             String details) {
        record(type, actor, roles, request, status, details, 0L);
    }

    public void recordHttpActivity(HttpServletRequest request,
                                   Authentication authentication,
                                   int status,
                                   long durationMs) {
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return;
        }

        String path = request.getRequestURI();
        if (path == null || path.startsWith("/api/auth/") || path.startsWith("/api/admin/monitoring")
                || path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/")
                || path.startsWith("/webjars/") || "/favicon.ico".equals(path) || "/error".equals(path)) {
            return;
        }

        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        record("REQUEST", authentication.getName(), roles, request, status, "Actividad de usuario", durationMs);
    }

    public List<MonitoringActivity> recentActivities(int limit) {
        return auditLogRepository.findAllByOrderByTimestampDesc(PageRequest.of(0, Math.max(1, limit)))
                .stream()
                .map(this::toMonitoringActivity)
                .toList();
    }

    private MonitoringActivity toMonitoringActivity(AuditLog log) {
        Set<String> roleSet = log.getRoles() != null && !log.getRoles().isBlank() 
                ? Set.of(log.getRoles().split(",")) 
                : Set.of();
        return new MonitoringActivity(
                log.getId(), log.getTimestamp(), log.getType(), log.getActor(),
                roleSet, log.getMethod(), log.getPath(), log.getIp(),
                log.getUserAgent(), log.getStatus(), log.getDurationMs(), log.getDetails()
        );
    }

    public void recordInternal(String type,
                               String actor,
                               Set<String> roles,
                               String method,
                               String path,
                               String ip,
                               String userAgent,
                               int status,
                               long durationMs,
                               String details) {
        
        String rolesStr = roles != null ? String.join(",", roles) : "";
        
        AuditLog auditLog = new AuditLog(
                Instant.now(), type, actor, rolesStr,
                method, path, ip, userAgent,
                status, durationMs, details
        );
        
        AuditLog saved = auditLogRepository.save(auditLog);

        if (webSocketPublisher != null) {
            try {
                webSocketPublisher.publish(TOPIC, objectMapper.writeValueAsString(toMonitoringActivity(saved)));
            } catch (JsonProcessingException ignored) {
            }
        }
    }

    private void record(String type,
                        String actor,
                        Set<String> roles,
                        HttpServletRequest request,
                        int status,
                        String details,
                        long durationMs) {
        
        String rolesStr = roles != null ? String.join(",", roles) : "";
        
        AuditLog auditLog = new AuditLog(
                Instant.now(), type, actor, rolesStr,
                request.getMethod(), request.getRequestURI() + buildQueryString(request),
                extractIp(request), request.getHeader("User-Agent"),
                status, durationMs, details
        );
        
        AuditLog saved = auditLogRepository.save(auditLog);

        if (webSocketPublisher != null) {
            try {
                webSocketPublisher.publish(TOPIC, objectMapper.writeValueAsString(toMonitoringActivity(saved)));
            } catch (JsonProcessingException ignored) {
            }
        }
    }

    private String buildQueryString(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return queryString == null || queryString.isBlank() ? "" : "?" + queryString;
    }

    private String extractIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }

        return request.getRemoteAddr();
    }
}