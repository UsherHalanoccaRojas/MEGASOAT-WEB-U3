package com.example.demo.web;

import com.example.demo.application.port.in.ReportPort;
import com.example.demo.domain.model.PointOfSale;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportPort reportPort;

    public ReportController(ReportPort reportPort) {
        this.reportPort = reportPort;
    }

    @GetMapping("/points-of-sale/ranking")
    public ResponseEntity<List<PointOfSale>> rankingByPerformance() {
        return ResponseEntity.ok(reportPort.getRankingByPerformance());
    }

    @GetMapping("/metrics/insurer")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','COMERCIAL')")
    public ResponseEntity<Map<String, Long>> metricsByInsurer() {
        return ResponseEntity.ok(reportPort.getMetricsByInsurer());
    }

    @GetMapping("/metrics/city")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','COMERCIAL')")
    public ResponseEntity<Map<String, Long>> metricsByCity() {
        return ResponseEntity.ok(reportPort.getMetricsByCity());
    }

    @GetMapping("/metrics/point-of-sale")
    public ResponseEntity<Map<String, Long>> metricsByPointOfSale() {
        return ResponseEntity.ok(reportPort.getMetricsByPointOfSale());
    }

    @GetMapping("/fraud/detected")
    public ResponseEntity<List<Object>> fraudDetected() {
        return ResponseEntity.ok(reportPort.getFraudDetected());
    }
}
