package com.example.demo.application.port.in;

import com.example.demo.domain.model.PointOfSale;
import java.util.List;
import java.util.Map;

public interface ReportPort {
    List<PointOfSale> getRankingByPerformance();
    Map<String, Long> getMetricsByInsurer();
    Map<String, Long> getMetricsByCity();
    Map<String, Long> getMetricsByPointOfSale();
    List<Object> getFraudDetected();
}
