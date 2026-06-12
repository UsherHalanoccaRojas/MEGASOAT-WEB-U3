package com.example.demo.web;

import com.example.demo.infrastructure.monitoring.ActivityMonitoringService;
import com.example.demo.infrastructure.monitoring.MonitoringActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/monitoring")
public class AdminMonitoringController {

    private final ActivityMonitoringService activityMonitoringService;
    private static final Logger logger = LoggerFactory.getLogger(AdminMonitoringController.class);

    public AdminMonitoringController(ActivityMonitoringService activityMonitoringService) {
        this.activityMonitoringService = activityMonitoringService;
    }

    @GetMapping("/activities")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<List<MonitoringActivity>> recentActivities(@RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(activityMonitoringService.recentActivities(limit));
    }

    @PostMapping("/ui-event")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> recordUiEvent(@RequestBody(required = false) Map<String, String> body,
                                              Authentication authentication,
                                              HttpServletRequest request) {
        logger.info("/api/admin/monitoring/ui-event called; Authorization present={} body={}",
                request.getHeader("Authorization") != null,
                body);

        if (authentication == null || authentication.getName() == null) {
            logger.warn("UI event rejected: no authentication (Authorization header present={})",
                    request.getHeader("Authorization") != null);
            return ResponseEntity.status(401).build();
        }

        String eventType = body != null ? body.getOrDefault("eventType", "UI_EVENT") : "UI_EVENT";
        String tab = body != null ? body.getOrDefault("tab", "") : "";
        String page = body != null ? body.getOrDefault("page", "") : "";
        String path = body != null ? body.getOrDefault("path", "") : "";
        String details = "Actividad UI";

        if (!tab.isBlank()) {
            details = "Navegacion de pestana: " + tab;
        } else if (!page.isBlank()) {
            details = "Navegacion de pagina: " + page + (path.isBlank() ? "" : " (" + path + ")");
        }
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        activityMonitoringService.recordAction(
                eventType,
                authentication.getName(),
                roles,
                request,
                200,
                details
        );

        return ResponseEntity.ok().build();
    }
}