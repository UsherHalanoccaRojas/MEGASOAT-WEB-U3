package com.example.demo.infrastructure.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class DualPasswordEncoder implements PasswordEncoder {
    
    private final BCryptPasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();
    
    @Override
    public String encode(CharSequence rawPassword) {
        return bcryptEncoder.encode(rawPassword);
    }
    
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // Try BCrypt first
        if (encodedPassword != null && encodedPassword.startsWith("$2a$")) {
            return bcryptEncoder.matches(rawPassword, encodedPassword);
        }
        // Fall back to plain text comparison for legacy passwords
        return rawPassword.toString().equals(encodedPassword);
    }
    
    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        return false;
    }
}
