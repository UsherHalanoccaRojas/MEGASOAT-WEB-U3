package com.example.demo.infrastructure.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CaptchaService {

    private static class Entry {
        final String answer;
        final long expiresAt;

        Entry(String answer, long expiresAt) {
            this.answer = answer;
            this.expiresAt = expiresAt;
        }
    }

    private final Map<String, Entry> store = new ConcurrentHashMap<>();
    private final Random rng = new Random();

    public record Captcha(String id, String question) {}

    public Captcha generate() {
        // Generate random alphanumeric code (6 chars)
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(rng.nextInt(chars.length())));
        }
        String code = sb.toString();
        String id = UUID.randomUUID().toString();
        store.put(id, new Entry(code, Instant.now().plusSeconds(120).toEpochMilli()));
        return new Captcha(id, code);
    }

    public boolean validate(String id, String provided) {
        if (id == null || provided == null) return false;
        Entry e = store.remove(id);
        if (e == null) return false;
        if (Instant.now().toEpochMilli() > e.expiresAt) return false;
        return e.answer.equals(provided.trim());
    }
}
