package com.example.demo.application.port.in;

public interface NotificationPort {

    void sendRenewalEmail(String email, String subject, String message);
    void publishRealtimeNotification(String topic, String message);
}
