package com.example.demo.application.service;

import com.example.demo.application.exception.ResourceNotFoundException;
import com.example.demo.application.port.in.PointOfSalePort;
import com.example.demo.domain.model.PointOfSale;
import com.example.demo.infrastructure.persistence.PointOfSaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PointOfSaleService implements PointOfSalePort {

    private final PointOfSaleRepository repository;

    public PointOfSaleService(PointOfSaleRepository repository) {
        this.repository = repository;
    }

    @Override
    public PointOfSale createPointOfSale(PointOfSale pointOfSale) {
        return repository.save(pointOfSale);
    }

    @Override
    public PointOfSale deactivatePointOfSale(Long id) {
        PointOfSale pos = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Punto de venta no encontrado: " + id));
        pos.setActive(false);
        return repository.save(pos);
    }

    @Override
    public PointOfSale activatePointOfSale(Long id) {
        PointOfSale pos = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Punto de venta no encontrado: " + id));
        pos.setActive(true);
        return repository.save(pos);
    }

    @Override
    public PointOfSale assignResponsible(Long id, String responsible) {
        PointOfSale pos = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Punto de venta no encontrado: " + id));
        pos.setResponsible(responsible);
        return repository.save(pos);
    }

    @Override
    public List<PointOfSale> listAllPointsOfSale() {
        return repository.findAll();
    }

    @Override
    public PointOfSale updateMetrics(Long id, double performanceScore, double delinquencyRate, int salesCount) {
        PointOfSale pos = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Punto de venta no encontrado: " + id));
        pos.setPerformanceScore(performanceScore);
        pos.setDelinquencyRate(delinquencyRate);
        pos.setSalesCount(salesCount);
        return repository.save(pos);
    }
}
