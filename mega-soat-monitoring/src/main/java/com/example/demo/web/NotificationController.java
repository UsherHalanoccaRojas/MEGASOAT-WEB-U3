package com.example.demo.web;

import com.example.demo.application.port.in.NotificationPort;
import com.example.demo.domain.model.Notification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationPort notificationPort;

    public NotificationController(NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Notification>> getUserNotifications(Authentication authentication) {
        return ResponseEntity.ok(notificationPort.getInternalNotifications(authentication.getName()));
    }

    @GetMapping("/unread")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Notification>> getUnreadNotifications(Authentication authentication) {
        return ResponseEntity.ok(notificationPort.getUnreadInternalNotifications(authentication.getName()));
    }

    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication authentication) {
        long count = notificationPort.getUnreadCount(authentication.getName());
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PostMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Authentication authentication) {
        boolean success = notificationPort.markAsRead(id, authentication.getName());
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        notificationPort.markAllAsRead(authentication.getName());
        return ResponseEntity.ok().build();
    }
}
