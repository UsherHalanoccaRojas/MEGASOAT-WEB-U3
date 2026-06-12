package com.example.demo.application.port.in;

import com.example.demo.domain.model.Policy;

import java.util.List;
import java.util.Optional;

public interface PolicyPortalPort {

    Optional<Policy> findByPolicyNumber(String policyNumber);

    List<Policy> findByVehiclePlate(String vehiclePlate);

    List<Policy> listActivePolicies();
}
