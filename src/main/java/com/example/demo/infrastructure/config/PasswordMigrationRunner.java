/*package com.example.demo.infrastructure.config;

import com.example.demo.domain.model.UserAccount;
import com.example.demo.infrastructure.persistence.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PasswordMigrationRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordMigrationRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        userRepository.findAll().forEach(user -> {
            String plain = user.getPassword();  
            if (plain != null && !plain.startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(plain));
                System.out.println("Migrado usuario: " + user.getEmail());
            }
        });
        // flush para asegurar commit inmediato
        userRepository.flush();
    }
}*/
