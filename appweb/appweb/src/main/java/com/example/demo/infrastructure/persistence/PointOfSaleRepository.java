package com.example.demo.infrastructure.persistence;

import com.example.demo.domain.model.PointOfSale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointOfSaleRepository extends JpaRepository<PointOfSale, Long> {
}
