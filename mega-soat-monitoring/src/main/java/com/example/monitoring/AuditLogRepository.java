package com.example.monitoring;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repositorio JPA: Spring genera automáticamente las consultas SQL
// basándose en el nombre de los métodos.
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT ?
    List<AuditLog> findAllByOrderByTimestampDesc(Pageable pageable);
}
