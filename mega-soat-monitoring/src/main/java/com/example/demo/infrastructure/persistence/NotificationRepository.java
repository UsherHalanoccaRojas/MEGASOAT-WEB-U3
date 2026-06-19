package com.example.demo.infrastructure.persistence;

import com.example.demo.domain.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserEmailOrderByCreatedAtDesc(String userEmail);

    List<Notification> findByUserEmailAndReadFalseOrderByCreatedAtDesc(String userEmail);

    long countByUserEmailAndReadFalse(String userEmail);
}
