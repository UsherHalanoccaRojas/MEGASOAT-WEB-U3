package com.example.demo.infrastructure.startup;

import com.example.demo.domain.model.Policy;
import com.example.demo.domain.model.RoleName;
import com.example.demo.domain.model.UserAccount;
import com.example.demo.infrastructure.persistence.PolicyRepository;
import com.example.demo.infrastructure.persistence.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           PolicyRepository policyRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.policyRepository = policyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            UserAccount admin = new UserAccount("Superadmin MegaSOAT", "admin@megasoat.local", passwordEncoder.encode("admin123"));
            admin.setUsername("superadmin");
            admin.setRole(RoleName.SUPERADMIN);
            userRepository.save(admin);
        }
        if (policyRepository.count() == 0) {
            Policy policy1 = new Policy();
            policy1.setPvId(1L);
            policy1.setPolicyNumber("POL-001-2024");
            policy1.setVehiclePlate("ABC-123");
            policy1.setInsurer("Pacifico");
            policy1.setVehicleType("Auto");
            policy1.setChannel("Particular");
            policy1.setPremium(java.math.BigDecimal.valueOf(850.00));
            policy1.setCommissionPv(java.math.BigDecimal.valueOf(127.50));
            policy1.setCommissionCompany(java.math.BigDecimal.valueOf(722.50));
            policy1.setStatus("Pagado");
            policy1.setDueDate(LocalDate.now().plusMonths(11));
            policy1.setIssueDate(LocalDateTime.now().minusMonths(1));
            policyRepository.save(policy1);

            Policy policy2 = new Policy();
            policy2.setPvId(2L);
            policy2.setPolicyNumber("POL-002-2024");
            policy2.setVehiclePlate("XYZ-789");
            policy2.setInsurer("Protección Plus");
            policy2.setVehicleType("Auto");
            policy2.setChannel("Flota");
            policy2.setPremium(java.math.BigDecimal.valueOf(1200.00));
            policy2.setCommissionPv(java.math.BigDecimal.valueOf(180.00));
            policy2.setCommissionCompany(java.math.BigDecimal.valueOf(1020.00));
            policy2.setStatus("Emitido");
            policy2.setDueDate(LocalDate.now().plusMonths(10));
            policy2.setIssueDate(LocalDateTime.now().minusMonths(2));
            policyRepository.save(policy2);
        }
    }
}
