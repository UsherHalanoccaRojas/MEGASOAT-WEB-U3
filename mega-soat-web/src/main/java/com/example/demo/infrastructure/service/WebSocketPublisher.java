package com.example.demo.infrastructure.service;

public interface WebSocketPublisher {
    void publish(String topic, String message);
}
