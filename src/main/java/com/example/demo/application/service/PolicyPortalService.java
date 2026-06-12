package com.example.demo.application.service;

import com.example.demo.application.port.in.PolicyPortalPort;
import com.example.demo.domain.model.Policy;
import com.example.demo.infrastructure.persistence.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PolicyPortalService implements PolicyPortalPort {

    private final PolicyRepository policyRepository;

    public PolicyPortalService(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Override
    public Optional<Policy> findByPolicyNumber(String policyNumber) {
        return policyRepository.findByPolicyNumber(policyNumber);
    }

    @Override
    public List<Policy> findByVehiclePlate(String vehiclePlate) {
        return policyRepository.findByVehiclePlate(vehiclePlate);
    }

    @Override
    public List<Policy> listActivePolicies() {
        return policyRepository.findByStatusNot("Anulado");
    }
}
