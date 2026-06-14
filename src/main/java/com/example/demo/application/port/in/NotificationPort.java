package com.example.demo.application.port.in;

public interface NotificationPort {

    void sendRenewalEmail(String email, String subject, String message);
    void publishRealtimeNotification(String topic, String message);
    
    com.example.demo.domain.model.Notification createInternalNotification(String userEmail, String title, String message);
    java.util.List<com.example.demo.domain.model.Notification> getInternalNotifications(String userEmail);
    java.util.List<com.example.demo.domain.model.Notification> getUnreadInternalNotifications(String userEmail);
    long getUnreadCount(String userEmail);
    boolean markAsRead(Long notificationId, String userEmail);
    void markAllAsRead(String userEmail);
}
