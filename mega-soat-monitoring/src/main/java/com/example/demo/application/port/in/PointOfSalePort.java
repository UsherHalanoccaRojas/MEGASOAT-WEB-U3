package com.example.demo.application.port.in;

import com.example.demo.domain.model.PointOfSale;

import java.util.List;

public interface PointOfSalePort {

    PointOfSale createPointOfSale(PointOfSale pointOfSale);

    PointOfSale deactivatePointOfSale(Long id);

    PointOfSale activatePointOfSale(Long id);

    PointOfSale assignResponsible(Long id, String responsible);

    List<PointOfSale> listAllPointsOfSale();

    PointOfSale updateMetrics(Long id, double performanceScore, double delinquencyRate, int salesCount);
}
