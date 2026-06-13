package com.example.demo.web;

import com.example.demo.domain.model.PointOfSale;
import com.example.demo.infrastructure.persistence.PointOfSaleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final PointOfSaleRepository pointOfSaleRepository;

    public DashboardController(PointOfSaleRepository pointOfSaleRepository) {
        this.pointOfSaleRepository = pointOfSaleRepository;
    }

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> overview() {
        long activePoints = pointOfSaleRepository.findAll().stream().filter(PointOfSale::isActive).count();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("activePointsOfSale", activePoints);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<Map<String, Object>>> ranking() {
        List<Map<String, Object>> ranking = new ArrayList<>();
        int position = 1;
        for (PointOfSale pv : pointOfSaleRepository.findAll()) {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("position", position++);
            data.put("name", pv.getName());
            data.put("city", pv.getCity());
            data.put("sales", 0);
            data.put("delinquency", 0.0);
            ranking.add(data);
        }
        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/map")
    public ResponseEntity<List<Map<String, Object>>> map() {
        Map<String, double[]> cityCoordinates = Map.of(
                "Lima", new double[]{-12.0464, -77.0428},
                "Arequipa", new double[]{-16.4090, -71.5375},
                "Tacna", new double[]{-18.0114, -70.2464},
                "Cali", new double[]{3.4516, -76.5320},
                "Bogotá", new double[]{4.7110, -74.0721},
                "Medellín", new double[]{6.2442, -75.5812},
                "Barranquilla", new double[]{10.9685, -74.7810},
                "Santa Marta", new double[]{11.2408, -74.1990},
                "Villavicencio", new double[]{4.1420, -73.6260},
                "Pasto", new double[]{1.2136, -77.2811}
        );

        List<Map<String, Object>> markers = pointOfSaleRepository.findAll().stream()
                .map(pv -> {
                    double[] coords = cityCoordinates.getOrDefault(pv.getCity(), new double[]{-12.0464, -77.0428});
                    Map<String, Object> marker = new LinkedHashMap<>();
                    marker.put("id", pv.getId());
                    marker.put("name", pv.getName());
                    marker.put("city", pv.getCity());
                    marker.put("sales", 0);
                    marker.put("delinquency", 0.0);
                    marker.put("risk", "bajo");
                    marker.put("lat", coords[0]);
                    marker.put("lng", coords[1]);
                    return marker;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(markers);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        long activePoints = pointOfSaleRepository.findAll().stream().filter(PointOfSale::isActive).count();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("activePointsOfSale", activePoints);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<Map<String, Object>>> alerts() {
        return ResponseEntity.ok(Collections.emptyList());
    }
}
