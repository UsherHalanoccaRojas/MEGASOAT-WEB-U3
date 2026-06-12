package com.example.demo.web;

import com.example.demo.domain.model.PointOfSale;
import com.example.demo.domain.model.Policy;
import com.example.demo.domain.model.Voucher;
import com.example.demo.infrastructure.persistence.PointOfSaleRepository;
import com.example.demo.infrastructure.persistence.PolicyRepository;
import com.example.demo.infrastructure.persistence.VoucherRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final PointOfSaleRepository pointOfSaleRepository;
    private final PolicyRepository policyRepository;
    private final VoucherRepository voucherRepository;

    public ReportController(PointOfSaleRepository pointOfSaleRepository,
                            PolicyRepository policyRepository,
                            VoucherRepository voucherRepository) {
        this.pointOfSaleRepository = pointOfSaleRepository;
        this.policyRepository = policyRepository;
        this.voucherRepository = voucherRepository;
    }

    @GetMapping("/points-of-sale/ranking")
    public ResponseEntity<List<PointOfSale>> rankingByPerformance() {
        List<PointOfSale> ranking = pointOfSaleRepository.findAll().stream()
                .sorted((p1, p2) -> Double.compare(p2.getPerformanceScore(), p1.getPerformanceScore()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/metrics/insurer")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','COMERCIAL')")
    public ResponseEntity<Map<String, Long>> metricsByInsurer() {
        return ResponseEntity.ok(policyRepository.findAll().stream()
                .collect(Collectors.groupingBy(Policy::getInsurer, Collectors.counting())));
    }

    @GetMapping("/metrics/city")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','COMERCIAL')")
    public ResponseEntity<Map<String, Long>> metricsByCity() {
        Map<Long, String> cityByPv = pointOfSaleRepository.findAll().stream()
                .collect(Collectors.toMap(PointOfSale::getId, PointOfSale::getCity));
        return ResponseEntity.ok(policyRepository.findAll().stream()
                .filter(p -> p.getPvId() != null && cityByPv.containsKey(p.getPvId()))
                .collect(Collectors.groupingBy(p -> cityByPv.get(p.getPvId()), Collectors.counting())));
    }

    @GetMapping("/metrics/point-of-sale")
    public ResponseEntity<Map<String, Long>> metricsByPointOfSale() {
        Map<Long, String> nameByPv = pointOfSaleRepository.findAll().stream()
                .collect(Collectors.toMap(PointOfSale::getId, PointOfSale::getName));
        return ResponseEntity.ok(policyRepository.findAll().stream()
                .filter(p -> p.getPvId() != null && nameByPv.containsKey(p.getPvId()))
                .collect(Collectors.groupingBy(p -> nameByPv.get(p.getPvId()), Collectors.counting())));
    }

    @GetMapping("/fraud/detected")
    public ResponseEntity<List<Voucher>> fraudDetected() {
        return ResponseEntity.ok(voucherRepository.findAll().stream()
                .filter(Voucher::isFraudSuspected)
                .collect(Collectors.toList()));
    }
}
