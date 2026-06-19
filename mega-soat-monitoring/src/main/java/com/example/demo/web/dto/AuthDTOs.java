package com.example.demo.web.dto;

import java.util.List;
import java.util.Set;

public class AuthDTOs {
    public record LoginRequestDTO(String email, String password, String captchaId, String captchaAnswer) {}
    public record RegisterRequestDTO(String fullName, String email, String password, List<String> roles) {}
    public record AuthResponseDTO(String token, String email, Set<String> roles) {}
}
