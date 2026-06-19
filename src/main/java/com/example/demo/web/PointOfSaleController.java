package com.example.demo.web;

import com.example.demo.application.port.in.PointOfSalePort;
import com.example.demo.domain.model.PointOfSale;
import com.example.demo.web.dto.BusinessDTOs.*;
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
    public ResponseEntity<PointOfSale> create(@RequestBody PointOfSaleRequestDTO request) {
        PointOfSale pos = new PointOfSale(request.name(), request.city());
        pos.setResponsible(request.responsible());
        pos.setEmail(request.email());
        pos.setTelefono(request.telefono());
        pos.setDireccion(request.direccion());
        
        // Generate a unique code (codigo) for the new point of sale
        String cityPrefix = request.city() != null && !request.city().trim().isEmpty() 
            ? request.city().trim().toUpperCase().replaceAll("[^A-Z]", "") 
            : "GEN";
        if (cityPrefix.length() > 5) {
            cityPrefix = cityPrefix.substring(0, 5);
        }
        String generatedCode = "PV-" + cityPrefix + "-" + String.format("%03d", (int)(Math.random() * 1000));
        pos.setCodigo(generatedCode);
        
        pos.setFechaRegistro(java.time.LocalDateTime.now());
        pos.setFechaActualizacion(java.time.LocalDateTime.now());

        return ResponseEntity.ok(pointOfSalePort.createPointOfSale(pos));
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<PointOfSale> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(pointOfSalePort.deactivatePointOfSale(id));
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<PointOfSale> activate(@PathVariable Long id) {
        return ResponseEntity.ok(pointOfSalePort.activatePointOfSale(id));
    }

    @PostMapping("/{id}/responsible")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','COMERCIAL')")
    public ResponseEntity<PointOfSale> assignResponsible(@PathVariable Long id, @RequestBody PointOfSaleRequestDTO request) {
        return ResponseEntity.ok(pointOfSalePort.assignResponsible(id, request.responsible()));
    }

    @PostMapping("/{id}/metrics")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','COMERCIAL')")
    public ResponseEntity<PointOfSale> updateMetrics(@PathVariable Long id, @RequestBody PointOfSale request) {
        return ResponseEntity.ok(pointOfSalePort.updateMetrics(id, request.getPerformanceScore(), request.getDelinquencyRate(), request.getSalesCount()));
    }
}
