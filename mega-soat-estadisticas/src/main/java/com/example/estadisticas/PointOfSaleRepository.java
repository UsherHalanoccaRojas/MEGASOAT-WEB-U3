package com.example.estadisticas;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointOfSaleRepository extends JpaRepository<PointOfSale, Long> {
    List<PointOfSale> findByActivo(boolean activo);
}
