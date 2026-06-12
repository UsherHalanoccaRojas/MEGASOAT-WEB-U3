package com.example.demo.infrastructure.persistence;

import com.example.demo.domain.model.Role;
import com.example.demo.domain.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
