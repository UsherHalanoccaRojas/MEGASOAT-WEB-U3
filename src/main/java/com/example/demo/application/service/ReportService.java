package com.example.demo.application.service;

import com.example.demo.application.port.in.ReportPort;
import com.example.demo.domain.model.PointOfSale;
import com.example.demo.infrastructure.persistence.PointOfSaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportService implements ReportPort {

    private final PointOfSaleRepository pointOfSaleRepository;

    public ReportService(PointOfSaleRepository pointOfSaleRepository) {
        this.pointOfSaleRepository = pointOfSaleRepository;
    }

    @Override
    public List<PointOfSale> getRankingByPerformance() {
        return pointOfSaleRepository.findAll().stream()
                .sorted((p1, p2) -> Double.compare(p2.getPerformanceScore(), p1.getPerformanceScore()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> getMetricsByInsurer() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Long> getMetricsByCity() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Long> getMetricsByPointOfSale() {
        return Collections.emptyMap();
    }

    @Override
    public List<Object> getFraudDetected() {
        return Collections.emptyList();
    }
}
