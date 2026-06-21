package com.example.monitoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Servicio principal del microservicio: guarda eventos y los transmite en tiempo real.
// Tiene dos responsabilidades:
//   1. Persistir el evento en la tabla audit_logs (MySQL)
//   2. Emitir el evento por WebSocket al panel de administración
@Service
public class AuditLogService {

    // Canal WebSocket donde escucha el panel de admin
    private static final String WS_TOPIC = "/topic/admin/monitoring";

    private final AuditLogRepository repository;
    private final SimpMessagingTemplate websocket;
    private final ObjectMapper json = new ObjectMapper().registerModule(new JavaTimeModule());

    public AuditLogService(AuditLogRepository repository, SimpMessagingTemplate websocket) {
        this.repository = repository;
        this.websocket = websocket;
    }

    // Devuelve los últimos N eventos ordenados del más reciente al más antiguo
    public List<AuditLog> recent(int limit) {
        return repository.findAllByOrderByTimestampDesc(PageRequest.of(0, Math.max(1, limit)));
    }

    // Guarda un evento en la BD y lo emite en tiempo real por WebSocket.
    // Recibe un Map con los campos del evento (flexible para UI y HTTP events).
    public void save(Map<String, Object> event) {
        String rolesStr = toRolesString(event.get("roles"));

        AuditLog log = new AuditLog(
                Instant.now(),
                str(event, "type", "EVENT"),
                str(event, "actor", "anonymous"),
                rolesStr,
                str(event, "method", ""),
                str(event, "path", ""),
                str(event, "ip", ""),
                str(event, "userAgent", ""),
                num(event, "status", 200),
                num(event, "durationMs", 0),
                str(event, "details", "")
        );

        AuditLog saved = repository.save(log);  // 1. Persiste en MySQL

        // 2. Emite en tiempo real al panel de admin por WebSocket
        try {
            websocket.convertAndSend(WS_TOPIC, json.writeValueAsString(saved));
        } catch (Exception ignored) {
            // Si WebSocket falla, el registro ya está guardado en BD
        }
    }

    // Helpers para extraer valores del Map de forma segura
    private String str(Map<String, Object> m, String key, String def) {
        Object v = m.get(key);
        return v instanceof String s ? s : def;
    }

    private int num(Map<String, Object> m, String key, int def) {
        Object v = m.get(key);
        return v instanceof Number n ? n.intValue() : def;
    }

    @SuppressWarnings("unchecked")
    private String toRolesString(Object rolesObj) {
        if (rolesObj instanceof List<?> list) {
            return String.join(",", (List<String>) list);
        }
        if (rolesObj instanceof Set<?> set) {
            return String.join(",", set.stream().map(Object::toString).toList());
        }
        return rolesObj instanceof String s ? s : "";
    }
}
