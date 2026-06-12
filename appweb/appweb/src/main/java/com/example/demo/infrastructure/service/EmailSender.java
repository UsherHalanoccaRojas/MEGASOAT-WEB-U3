package com.example.demo.infrastructure.service;

public interface EmailSender {
    void sendEmail(String to, String subject, String body);
}
