package com.example.demo.infrastructure.monitoring;

import com.example.demo.infrastructure.service.WebSocketPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ActivityMonitoringService {

    private static final int MAX_EVENTS = 200;
    private static final String TOPIC = "/topic/admin/monitoring";

    private final WebSocketPublisher webSocketPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AtomicLong sequence = new AtomicLong(1);
    private final ArrayDeque<MonitoringActivity> activities = new ArrayDeque<>();
    private final Object lock = new Object();

    public ActivityMonitoringService(ObjectProvider<WebSocketPublisher> webSocketPublisherProvider) {
        this.webSocketPublisher = webSocketPublisherProvider.getIfAvailable();
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
        synchronized (lock) {
            return activities.stream().limit(Math.max(1, Math.min(limit, MAX_EVENTS))).toList();
        }
    }

    private void record(String type,
                        String actor,
                        Set<String> roles,
                        HttpServletRequest request,
                        int status,
                        String details,
                        long durationMs) {
        MonitoringActivity activity = new MonitoringActivity(
                sequence.getAndIncrement(),
                Instant.now(),
                type,
                actor,
                roles,
                request.getMethod(),
                request.getRequestURI() + buildQueryString(request),
                extractIp(request),
                request.getHeader("User-Agent"),
                status,
                durationMs,
                details
        );

        MonitoringActivity activityToPublish;
        synchronized (lock) {
            activityToPublish = upsertByActorAndIp(activity);
        }

        if (webSocketPublisher != null) {
            try {
                webSocketPublisher.publish(TOPIC, objectMapper.writeValueAsString(activityToPublish));
            } catch (JsonProcessingException ignored) {
                // Si la serialización falla, el buffer en memoria sigue funcionando.
            }
        }
    }

    private MonitoringActivity upsertByActorAndIp(MonitoringActivity incoming) {
        MonitoringActivity existing = null;
        if (canMergeByActor(incoming)) {
            Iterator<MonitoringActivity> iterator = activities.iterator();
            while (iterator.hasNext()) {
                MonitoringActivity current = iterator.next();
                if (sameActor(current, incoming)) {
                    if (existing == null) {
                        existing = current;
                    }
                    iterator.remove();
                }
            }
        }

        MonitoringActivity merged = existing == null
                ? incoming
                : new MonitoringActivity(
                        existing.getId(),
                        incoming.getTimestamp(),
                        incoming.getType(),
                        incoming.getActor(),
                        incoming.getRoles(),
                        incoming.getMethod(),
                        incoming.getPath(),
                        incoming.getIp(),
                        incoming.getUserAgent(),
                        incoming.getStatus(),
                        incoming.getDurationMs(),
                        incoming.getDetails()
                );

        activities.addFirst(merged);
        while (activities.size() > MAX_EVENTS) {
            activities.removeLast();
        }

        return merged;
    }

    private boolean canMergeByActor(MonitoringActivity activity) {
        return hasText(activity.getActor());
    }

    private boolean sameActor(MonitoringActivity left, MonitoringActivity right) {
        return left.getActor().equalsIgnoreCase(right.getActor());
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
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