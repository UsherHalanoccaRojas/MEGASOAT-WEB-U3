package com.example.estadisticas;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserAccount, Long> {
    List<UserAccount> findByActive(boolean active);
}
