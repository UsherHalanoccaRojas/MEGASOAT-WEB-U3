package com.example.demo.infrastructure.monitoring;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ActivityMonitoringService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String TARGET_URL = "http://localhost:8081/api/admin/monitoring/internal-log";

    public void recordAuthenticationEvent(String type,
                                          String actor,
                                          Set<String> roles,
                                          HttpServletRequest request,
                                          int status,
                                          String details) {
        sendInternalLog(type, actor, roles, request.getMethod(), request.getRequestURI() + buildQueryString(request), extractIp(request), request.getHeader("User-Agent"), status, 0L, details);
    }

    public void recordAction(String type,
                             String actor,
                             Set<String> roles,
                             HttpServletRequest request,
                             int status,
                             String details) {
        sendInternalLog(type, actor, roles, request.getMethod(), request.getRequestURI() + buildQueryString(request), extractIp(request), request.getHeader("User-Agent"), status, 0L, details);
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
        sendInternalLog("REQUEST", authentication.getName(), roles, request.getMethod(), path + buildQueryString(request), extractIp(request), request.getHeader("User-Agent"), status, durationMs, "Actividad de usuario");
    }

    private void sendInternalLog(String type,
                                 String actor,
                                 Set<String> roles,
                                 String method,
                                 String path,
                                 String ip,
                                 String userAgent,
                                 int status,
                                 long durationMs,
                                 String details) {
        try {
            String rolesJson = roles != null 
                    ? roles.stream().map(r -> "\"" + r + "\"").collect(Collectors.joining(",", "[", "]"))
                    : "[]";
            String escapedUserAgent = userAgent != null ? userAgent.replace("\"", "\\\"") : "";
            String escapedPath = path != null ? path.replace("\"", "\\\"") : "";
            String escapedDetails = details != null ? details.replace("\"", "\\\"") : "";

            String json = String.format(
                "{\"type\":\"%s\",\"actor\":\"%s\",\"roles\":%s,\"method\":\"%s\",\"path\":\"%s\",\"ip\":\"%s\",\"userAgent\":\"%s\",\"status\":%d,\"durationMs\":%d,\"details\":\"%s\"}",
                type, actor, rolesJson, method, escapedPath, ip, escapedUserAgent, status, durationMs, escapedDetails
            );

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(TARGET_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.discarding());
        } catch (Exception ignored) {
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