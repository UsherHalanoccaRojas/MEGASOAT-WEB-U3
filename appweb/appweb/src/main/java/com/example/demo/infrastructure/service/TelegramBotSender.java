package com.example.demo.infrastructure.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class TelegramBotSender implements TelegramSender {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void sendMessage(String message) {
        if (botToken == null || botToken.isBlank() || chatId == null || chatId.isBlank()) {
            return;
        }
        String url = UriComponentsBuilder.fromUriString("https://api.telegram.org/bot" + botToken + "/sendMessage")
                .queryParam("chat_id", chatId)
                .queryParam("text", message)
                .toUriString();
        restTemplate.getForObject(url, String.class);
    }
}
