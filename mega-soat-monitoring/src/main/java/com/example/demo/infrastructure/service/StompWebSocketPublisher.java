package com.example.demo.infrastructure.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class StompWebSocketPublisher implements WebSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public StompWebSocketPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void publish(String topic, String message) {
        messagingTemplate.convertAndSend(topic, message);
    }
}
