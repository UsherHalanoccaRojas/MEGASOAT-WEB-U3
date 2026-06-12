package com.example.demo.web;

import com.example.demo.domain.model.PointOfSale;
import com.example.demo.domain.model.Policy;
import com.example.demo.infrastructure.persistence.PointOfSaleRepository;
import com.example.demo.infrastructure.persistence.PolicyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final PointOfSaleRepository pointOfSaleRepository;
    private final PolicyRepository policyRepository;

    public DashboardController(PointOfSaleRepository pointOfSaleRepository,
                               PolicyRepository policyRepository) {
        this.pointOfSaleRepository = pointOfSaleRepository;
        this.policyRepository = policyRepository;
    }

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> overview() {
        List<Policy> policies = policyRepository.findAll();
        long totalPolicies = policies.size();
        long overdue = policies.stream()
                .filter(p -> p.getDueDate() != null && p.getDueDate().isBefore(LocalDate.now()))
                .count();
        long activePoints = pointOfSaleRepository.findAll().stream().filter(PointOfSale::isActive).count();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalPolicies", totalPolicies);
        result.put("averageDelinquency", totalPolicies == 0 ? 0.0 : Math.round((double) overdue / totalPolicies * 10000.0) / 100.0);
        result.put("activePointsOfSale", activePoints);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<Map<String, Object>>> ranking() {
        Map<Long, Long> salesByPv = policyRepository.findAll().stream()
                .filter(p -> p.getPvId() != null)
                .collect(Collectors.groupingBy(Policy::getPvId, Collectors.counting()));

        Map<Long, Long> overdueByPv = policyRepository.findAll().stream()
                .filter(p -> p.getPvId() != null && p.getDueDate() != null && p.getDueDate().isBefore(LocalDate.now()))
                .collect(Collectors.groupingBy(Policy::getPvId, Collectors.counting()));

        List<ProjectedPoint> points = pointOfSaleRepository.findAll().stream()
                .map(pv -> {
                    long total = salesByPv.getOrDefault(pv.getId(), 0L);
                    long overdue = overdueByPv.getOrDefault(pv.getId(), 0L);
                    double delinquency = total == 0 ? 0.0 : (double) overdue / total * 100.0;
                    return new ProjectedPoint(pv, total, delinquency);
                })
                .sorted(Comparator.comparing(ProjectedPoint::getTotalSales).reversed())
                .collect(Collectors.toList());

        List<Map<String, Object>> ranking = new ArrayList<>();
        int position = 1;
        for (ProjectedPoint item : points) {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("position", position++);
            data.put("name", item.point.getName());
            data.put("city", item.point.getCity());
            data.put("sales", item.totalSales);
            data.put("delinquency", Math.round(item.delinquency * 10.0) / 10.0);
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

        Map<Long, Long> totalByPv = policyRepository.findAll().stream()
                .filter(p -> p.getPvId() != null)
                .collect(Collectors.groupingBy(Policy::getPvId, Collectors.counting()));

        Map<Long, Long> overdueByPv = policyRepository.findAll().stream()
                .filter(p -> p.getPvId() != null && p.getDueDate() != null && p.getDueDate().isBefore(LocalDate.now()))
                .collect(Collectors.groupingBy(Policy::getPvId, Collectors.counting()));

        List<Map<String, Object>> markers = pointOfSaleRepository.findAll().stream()
                .map(pv -> {
                    long total = totalByPv.getOrDefault(pv.getId(), 0L);
                    long overdue = overdueByPv.getOrDefault(pv.getId(), 0L);
                    double ratio = total == 0 ? 0 : (double) overdue / total;
                    String level = ratio >= 0.20 ? "alto" : ratio >= 0.10 ? "medio" : "bajo";
                    double[] coords = cityCoordinates.getOrDefault(pv.getCity(), new double[]{-12.0464, -77.0428});
                    Map<String, Object> marker = new LinkedHashMap<>();
                    marker.put("id", pv.getId());
                    marker.put("name", pv.getName());
                    marker.put("city", pv.getCity());
                    marker.put("sales", total);
                    marker.put("delinquency", Math.round(ratio * 1000.0) / 10.0);
                    marker.put("risk", level);
                    marker.put("lat", coords[0]);
                    marker.put("lng", coords[1]);
                    return marker;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(markers);
    }

    private static class ProjectedPoint {
        private final PointOfSale point;
        private final long totalSales;
        private final double delinquency;

        public ProjectedPoint(PointOfSale point, long totalSales, double delinquency) {
            this.point = point;
            this.totalSales = totalSales;
            this.delinquency = delinquency;
        }

        public long getTotalSales() {
            return totalSales;
        }

        public double getDelinquency() {
            return delinquency;
        }
    }
}
