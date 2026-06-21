package com.example.monitoring;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Controlador REST del microservicio de monitoreo.
// Expone tres endpoints:
//   GET  /api/admin/monitoring/activities   → consulta eventos recientes
//   POST /api/admin/monitoring/ui-event     → registra acción del usuario en la UI
//   POST /api/admin/monitoring/internal-log → recibe logs de la app principal (mega-soat-web)
@RestController
@RequestMapping("/api/admin/monitoring")
public class AuditLogController {

    private final AuditLogService service;

    public AuditLogController(AuditLogService service) {
        this.service = service;
    }

    // Devuelve los últimos N eventos. Solo accesible por SUPERADMIN.
    @GetMapping("/activities")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<List<AuditLog>> activities(@RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(service.recent(limit));
    }

    // Registra una acción que el usuario hizo en la interfaz (clic en pestaña, navegación, etc.)
    // Requiere estar autenticado (token JWT válido).
    @PostMapping("/ui-event")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> uiEvent(@RequestBody(required = false) Map<String, String> body,
                                        Authentication auth,
                                        HttpServletRequest request) {
        if (auth == null) return ResponseEntity.status(401).build();

        String tab  = body != null ? body.getOrDefault("tab", "") : "";
        String page = body != null ? body.getOrDefault("page", "") : "";
        String details = !tab.isBlank()  ? "Navegación de pestaña: " + tab
                       : !page.isBlank() ? "Navegación de página: " + page
                       : "Actividad UI";

        // Construye el mapa de evento y lo delega al servicio
        service.save(Map.of(
                "type",      body != null ? body.getOrDefault("eventType", "UI_EVENT") : "UI_EVENT",
                "actor",     auth.getName(),
                "roles",     auth.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.joining(",")),
                "method",    request.getMethod(),
                "path",      request.getRequestURI(),
                "ip",        extractIp(request),
                "userAgent", String.valueOf(request.getHeader("User-Agent")),
                "status",    200,
                "details",   details
        ));

        return ResponseEntity.ok().build();
    }

    // Recibe logs enviados por mega-soat-web (la app principal) cada vez que
    // un usuario autenticado hace una petición HTTP. No requiere token propio
    // porque es comunicación interna entre microservicios.
    @PostMapping("/internal-log")
    public ResponseEntity<Void> internalLog(@RequestBody Map<String, Object> body) {
        service.save(body);
        return ResponseEntity.ok().build();
    }

    // Extrae la IP real del cliente, soportando proxies y balanceadores
    private String extractIp(HttpServletRequest req) {
        String forwarded = req.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) return forwarded.split(",")[0].trim();
        String real = req.getHeader("X-Real-IP");
        if (real != null && !real.isBlank()) return real.trim();
        return req.getRemoteAddr();
    }
}
