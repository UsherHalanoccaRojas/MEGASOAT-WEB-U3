package com.example.demo.web;

import com.example.demo.application.port.in.DashboardPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardPort dashboardPort;

    public DashboardController(DashboardPort dashboardPort) {
        this.dashboardPort = dashboardPort;
    }

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> overview() {
        return ResponseEntity.ok(dashboardPort.getOverview());
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<Map<String, Object>>> ranking() {
        return ResponseEntity.ok(dashboardPort.getRanking());
    }

    @GetMapping("/map")
    public ResponseEntity<List<Map<String, Object>>> map() {
        return ResponseEntity.ok(dashboardPort.getMapMarkers());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(dashboardPort.getStats());
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<Map<String, Object>>> alerts() {
        return ResponseEntity.ok(dashboardPort.getAlerts());
    }
}
