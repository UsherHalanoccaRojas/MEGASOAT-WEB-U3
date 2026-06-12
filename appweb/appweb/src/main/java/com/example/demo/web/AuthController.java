package com.example.demo.web;

import com.example.demo.application.port.in.UserManagementPort;
import com.example.demo.domain.model.RoleName;
import com.example.demo.domain.model.UserAccount;
import com.example.demo.infrastructure.service.CaptchaService;
import com.example.demo.infrastructure.security.JwtTokenProvider;
import com.example.demo.infrastructure.persistence.UserRepository;
import com.example.demo.web.dto.AuthResponse;
import com.example.demo.web.dto.LoginRequest;
import com.example.demo.web.dto.RegisterRequest;
import com.example.demo.web.dto.UserProfileResponse;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final UserManagementPort userManagementPort;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final CaptchaService captchaService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserManagementPort userManagementPort,
                          UserRepository userRepository,
                          JwtTokenProvider jwtTokenProvider,
                          CaptchaService captchaService) {
        this.authenticationManager = authenticationManager;
        this.userManagementPort = userManagementPort;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.captchaService = captchaService;
    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaService.Captcha> generateCaptcha() {
        return ResponseEntity.ok(captchaService.generate());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // validate captcha first (required)
        if (request.getCaptchaId() == null || request.getCaptchaAnswer() == null ||
                !captchaService.validate(request.getCaptchaId(), request.getCaptchaAnswer())) {
            logger.info("Login attempt failed for {}: captcha invalid or expired", request.getEmail());
            return ResponseEntity.status(400).build();
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Set<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());
            String token = jwtTokenProvider.generateToken(userDetails.getUsername(), roles);
            logger.info("Login successful for {} roles={}", userDetails.getUsername(), roles);
            return ResponseEntity.ok(new AuthResponse(token, userDetails.getUsername(), roles));
        } catch (BadCredentialsException ex) {
            logger.info("Login failed for {}: bad credentials", request.getEmail());
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        UserAccount user = new UserAccount(request.getFullName(), request.getEmail(), request.getPassword());
        List<RoleName> roles = request.getRoles().stream()
                .map(role -> RoleName.fromValue(role.replace("ROLE_", "")))
                .collect(Collectors.toList());
        UserAccount saved = userManagementPort.register(user, roles);
        String token = jwtTokenProvider.generateToken(saved.getEmail(), java.util.Set.of(saved.getRole().getAuthority()));
        return ResponseEntity.ok(new AuthResponse(token, saved.getEmail(), java.util.Set.of(saved.getRole().getAuthority())));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        
        UserAccount user = userRepository.findByEmail(principal.getName())
                .orElse(null);
        
        if (user == null) {
            return ResponseEntity.status(404).build();
        }
        
        return ResponseEntity.ok(new UserProfileResponse(
                user.getEmail(),
                user.getFullName(),
                user.getUsername(),
                user.getRole() != null ? user.getRole().getAuthority() : "USER",
                user.isActive()
        ));
    }

    @PostMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestBody Map<String, String> request,
            Principal principal) {
        
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        
        UserAccount user = userRepository.findByEmail(principal.getName())
                .orElse(null);
        
        if (user == null) {
            return ResponseEntity.status(404).build();
        }
        
        // Actualizar campos
        if (request.containsKey("fullName") && request.get("fullName") != null) {
            user.setFullName(request.get("fullName"));
        }
        if (request.containsKey("username") && request.get("username") != null) {
            user.setUsername(request.get("username"));
        }
        
        userRepository.save(user);
        
        return ResponseEntity.ok(new UserProfileResponse(
                user.getEmail(),
                user.getFullName(),
                user.getUsername(),
                user.getRole() != null ? user.getRole().getAuthority() : "USER",
                user.isActive()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // El logout se maneja desde el cliente (eliminar token)
        return ResponseEntity.ok().build();
    }
}
