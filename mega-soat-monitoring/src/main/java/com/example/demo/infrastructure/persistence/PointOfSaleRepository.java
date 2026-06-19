package com.example.demo.infrastructure.persistence;

import com.example.demo.domain.model.PointOfSale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointOfSaleRepository extends JpaRepository<PointOfSale, Long> {
    List<PointOfSale> findTop5ByNameContainingIgnoreCaseOrCityContainingIgnoreCase(String name, String city);
}
