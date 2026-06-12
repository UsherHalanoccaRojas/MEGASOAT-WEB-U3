package com.example.demo.infrastructure.persistence;

import com.example.demo.domain.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    Optional<Policy> findByPolicyNumber(String policyNumber);
    List<Policy> findByVehiclePlate(String vehiclePlate);
    List<Policy> findByStatusNot(String status);
}
