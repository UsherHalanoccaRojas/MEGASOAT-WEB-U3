package com.example.estadisticas;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticasController {

    private final EstadisticasService service;

    public EstadisticasController(EstadisticasService service) {
        this.service = service;
    }

    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> resumen() {
        return ResponseEntity.ok(service.getResumen());
    }

    @GetMapping("/pv-por-ciudad")
    public ResponseEntity<Map<String, Long>> pvPorCiudad() {
        return ResponseEntity.ok(service.getPvPorCiudad());
    }

    @GetMapping("/usuarios-por-rol")
    public ResponseEntity<Map<String, Long>> usuariosPorRol() {
        return ResponseEntity.ok(service.getUsuariosPorRol());
    }

    @GetMapping("/pv-activos-vs-inactivos")
    public ResponseEntity<Map<String, Long>> pvActivosVsInactivos() {
        return ResponseEntity.ok(service.getPvActivosVsInactivos());
    }
}
