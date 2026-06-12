package com.example.demo.application.service;

import com.example.demo.application.port.in.NotificationPort;
import com.example.demo.infrastructure.service.EmailSender;
import com.example.demo.infrastructure.service.WebSocketPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificationService implements NotificationPort {

    private final EmailSender emailSender;
    private final WebSocketPublisher webSocketPublisher;

    public NotificationService(EmailSender emailSender, WebSocketPublisher webSocketPublisher) {
        this.emailSender = emailSender;
        this.webSocketPublisher = webSocketPublisher;
    }

    @Override
    public void sendRenewalEmail(String email, String subject, String message) {
        emailSender.sendEmail(email, subject, message);
    }

    @Override
    public void publishRealtimeNotification(String topic, String message) {
        webSocketPublisher.publish(topic, message);
    }
}

