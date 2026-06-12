package com.example.demo.web;

import com.example.demo.application.port.in.PointOfSalePort;
import com.example.demo.domain.model.PointOfSale;
import com.example.demo.web.dto.PointOfSaleRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/points-of-sale")
public class PointOfSaleController {

    private final PointOfSalePort pointOfSalePort;

    public PointOfSaleController(PointOfSalePort pointOfSalePort) {
        this.pointOfSalePort = pointOfSalePort;
    }

    @GetMapping
    public ResponseEntity<List<PointOfSale>> listPointsOfSale() {
        return ResponseEntity.ok(pointOfSalePort.listAllPointsOfSale());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','COMERCIAL')")
    public ResponseEntity<PointOfSale> create(@RequestBody PointOfSaleRequest request) {
        PointOfSale pos = new PointOfSale(request.getName(), request.getCity());
        pos.setResponsible(request.getResponsible());
        return ResponseEntity.ok(pointOfSalePort.createPointOfSale(pos));
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<PointOfSale> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(pointOfSalePort.deactivatePointOfSale(id));
    }

    @PostMapping("/{id}/responsible")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','COMERCIAL')")
    public ResponseEntity<PointOfSale> assignResponsible(@PathVariable Long id, @RequestBody PointOfSaleRequest request) {
        return ResponseEntity.ok(pointOfSalePort.assignResponsible(id, request.getResponsible()));
    }

    @PostMapping("/{id}/metrics")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','COMERCIAL')")
    public ResponseEntity<PointOfSale> updateMetrics(@PathVariable Long id, @RequestBody PointOfSale request) {
        return ResponseEntity.ok(pointOfSalePort.updateMetrics(id, request.getPerformanceScore(), request.getDelinquencyRate(), request.getSalesCount()));
    }
}
