package com.example.demo.application.service;

import com.example.demo.application.port.in.NotificationPort;
import com.example.demo.infrastructure.service.WebSocketPublisher;
import com.example.demo.infrastructure.persistence.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationService implements NotificationPort {

    private final WebSocketPublisher webSocketPublisher;
    private final NotificationRepository notificationRepository;

    public NotificationService(WebSocketPublisher webSocketPublisher, NotificationRepository notificationRepository) {
        this.webSocketPublisher = webSocketPublisher;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void publishRealtimeNotification(String topic, String message) {
        webSocketPublisher.publish(topic, message);
    }

    @Override
    public com.example.demo.domain.model.Notification createInternalNotification(String userEmail, String title, String message) {
        com.example.demo.domain.model.Notification notification = new com.example.demo.domain.model.Notification(userEmail, title, message);
        return notificationRepository.save(notification);
    }

    @Override
    public List<com.example.demo.domain.model.Notification> getInternalNotifications(String userEmail) {
        return notificationRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
    }

    @Override
    public List<com.example.demo.domain.model.Notification> getUnreadInternalNotifications(String userEmail) {
        return notificationRepository.findByUserEmailAndReadFalseOrderByCreatedAtDesc(userEmail);
    }

    @Override
    public long getUnreadCount(String userEmail) {
        return notificationRepository.countByUserEmailAndReadFalse(userEmail);
    }

    @Override
    public boolean markAsRead(Long notificationId, String userEmail) {
        return notificationRepository.findById(notificationId).map(notification -> {
            if (notification.getUserEmail().equals(userEmail)) {
                notification.setRead(true);
                notificationRepository.save(notification);
                return true;
            }
            return false;
        }).orElse(false);
    }

    @Override
    public void markAllAsRead(String userEmail) {
        List<com.example.demo.domain.model.Notification> unread = notificationRepository.findByUserEmailAndReadFalseOrderByCreatedAtDesc(userEmail);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}

