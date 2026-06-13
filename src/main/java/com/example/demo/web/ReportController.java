package com.example.demo.web;

import com.example.demo.domain.model.PointOfSale;
import com.example.demo.infrastructure.persistence.PointOfSaleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final PointOfSaleRepository pointOfSaleRepository;

    public ReportController(PointOfSaleRepository pointOfSaleRepository) {
        this.pointOfSaleRepository = pointOfSaleRepository;
    }

    @GetMapping("/points-of-sale/ranking")
    public ResponseEntity<List<PointOfSale>> rankingByPerformance() {
        List<PointOfSale> ranking = pointOfSaleRepository.findAll().stream()
                .sorted((p1, p2) -> Double.compare(p2.getPerformanceScore(), p1.getPerformanceScore()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/metrics/insurer")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','COMERCIAL')")
    public ResponseEntity<Map<String, Long>> metricsByInsurer() {
        return ResponseEntity.ok(Collections.emptyMap());
    }

    @GetMapping("/metrics/city")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','COMERCIAL')")
    public ResponseEntity<Map<String, Long>> metricsByCity() {
        return ResponseEntity.ok(Collections.emptyMap());
    }

    @GetMapping("/metrics/point-of-sale")
    public ResponseEntity<Map<String, Long>> metricsByPointOfSale() {
        return ResponseEntity.ok(Collections.emptyMap());
    }

    @GetMapping("/fraud/detected")
    public ResponseEntity<List<Object>> fraudDetected() {
        return ResponseEntity.ok(Collections.emptyList());
    }
}
