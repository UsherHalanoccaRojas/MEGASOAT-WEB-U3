package com.example.monitoring;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// Configuración WebSocket con STOMP (protocolo de mensajería sobre WebSocket).
// Permite al panel de admin recibir eventos en tiempo real sin hacer polling.
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // /topic/... → canal de broadcast (un mensaje llega a todos los suscritos)
        config.enableSimpleBroker("/topic");
        // /app/... → prefijo para mensajes enviados desde el cliente al servidor
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint WebSocket: el frontend se conecta a ws://localhost:8081/ws/notifications
        // SockJS como fallback para navegadores sin soporte WebSocket nativo
        registry.addEndpoint("/ws/notifications").setAllowedOriginPatterns("*").withSockJS();
    }
}
