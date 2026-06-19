package com.example.demo.infrastructure.persistence;

import com.example.demo.domain.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmail(String email);
    List<UserAccount> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);
}
