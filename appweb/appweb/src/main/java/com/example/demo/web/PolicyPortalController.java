package com.example.demo.web;

import com.example.demo.application.port.in.PolicyPortalPort;
import com.example.demo.domain.model.Policy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portal")
public class PolicyPortalController {

    private final PolicyPortalPort policyPortalPort;

    public PolicyPortalController(PolicyPortalPort policyPortalPort) {
        this.policyPortalPort = policyPortalPort;
    }

    @GetMapping("/policies")
    public ResponseEntity<List<Policy>> listActivePolicies() {
        return ResponseEntity.ok(policyPortalPort.listActivePolicies());
    }
    @GetMapping("/policies/{policyNumber}")
    public ResponseEntity<Policy> findByPolicyNumber(@PathVariable String policyNumber) {
        return policyPortalPort.findByPolicyNumber(policyNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
