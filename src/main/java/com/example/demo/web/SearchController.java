package com.example.demo.web;

import com.example.demo.infrastructure.persistence.PointOfSaleRepository;
import com.example.demo.infrastructure.persistence.PolicyRepository;
import com.example.demo.infrastructure.persistence.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;
    private final PointOfSaleRepository posRepository;

    public SearchController(PolicyRepository policyRepository,
                            UserRepository userRepository,
                            PointOfSaleRepository posRepository) {
        this.policyRepository = policyRepository;
        this.userRepository = userRepository;
        this.posRepository = posRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam String q,
            Authentication auth) {

        if (q == null || q.isBlank() || q.length() < 2)
            return ResponseEntity.badRequest().build();

        String term = q.trim();

        var policies = policyRepository
                .findTop5ByPolicyNumberContainingIgnoreCaseOrVehiclePlateContainingIgnoreCase(term, term)
                .stream()
                .map(p -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id",           p.getId());
                    m.put("policyNumber", p.getPolicyNumber());
                    m.put("plate",        p.getVehiclePlate());
                    m.put("insurer",      p.getInsurer());
                    m.put("status",       p.getStatus());
                    return m;
                }).toList();

        var pos = posRepository
                .findTop5ByNameContainingIgnoreCaseOrCityContainingIgnoreCase(term, term)
                .stream()
                .map(p -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id",   p.getId());
                    m.put("name", p.getName());
                    m.put("city", p.getCity());
                    return m;
                }).toList();

        List<Map<String, Object>> users = List.of();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                            || a.getAuthority().equals("ROLE_SUPERADMIN"));

        if (isAdmin) {
            users = userRepository
                    .findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(term, term)
                    .stream()
                    .limit(5)
                    .map(u -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("id",       u.getId());
                        m.put("fullName", u.getFullName());
                        m.put("email",    u.getEmail());
                        m.put("rol",      u.getRol());
                        return m;
                    }).toList();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("policies", policies);
        result.put("users",    users);
        result.put("pos",      pos);

        return ResponseEntity.ok(result);
    }
}
