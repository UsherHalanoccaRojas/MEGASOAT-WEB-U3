package com.example.demo.web;

import com.example.demo.application.port.in.UserManagementPort;
import com.example.demo.domain.model.RoleName;
import com.example.demo.domain.model.UserAccount;
import com.example.demo.infrastructure.monitoring.ActivityMonitoringService;
import com.example.demo.infrastructure.security.JwtTokenProvider;
import com.example.demo.web.dto.AuthDTOs.*;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final com.example.demo.infrastructure.service.CaptchaService captchaService;
    private final ActivityMonitoringService activityMonitoringService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserManagementPort userManagementPort,
                          JwtTokenProvider jwtTokenProvider,
                          com.example.demo.infrastructure.service.CaptchaService captchaService,
                          ActivityMonitoringService activityMonitoringService) {
        this.authenticationManager = authenticationManager;
        this.userManagementPort = userManagementPort;
        this.jwtTokenProvider = jwtTokenProvider;
        this.captchaService = captchaService;
        this.activityMonitoringService = activityMonitoringService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request, HttpServletRequest httpRequest) {
        if (request.captchaId() == null || request.captchaAnswer() == null ||
                !captchaService.validate(request.captchaId(), request.captchaAnswer())) {
            logger.info("Login attempt failed for {}: captcha invalid or expired", request.email());
            return ResponseEntity.status(400).build();
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            UserAccount account = userManagementPort.findByEmail(userDetails.getUsername()).orElse(null);

            Set<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());
            String token = jwtTokenProvider.generateToken(userDetails.getUsername(), roles);

            // Guardar token activo
            if (account != null) {
                account.setSessionToken(token);
                userManagementPort.updateUser(account);
            }

            logger.info("Login successful for {} roles={}", userDetails.getUsername(), roles);
            activityMonitoringService.recordAuthenticationEvent(
                    "LOGIN",
                    userDetails.getUsername(),
                    roles,
                    httpRequest,
                    200,
                    "Inicio de sesión exitoso"
            );
            return ResponseEntity.ok(new AuthResponseDTO(token, userDetails.getUsername(), roles));
        } catch (BadCredentialsException ex) {
            logger.info("Login failed for {}: bad credentials", request.email());
            return ResponseEntity.status(401).build();
        }
    }



    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            userManagementPort.findByEmail(userDetails.getUsername()).ifPresent(acc -> {
                acc.setSessionToken(null);
                userManagementPort.updateUser(acc);
            });
            logger.info("Logout: session cleared for {}", userDetails.getUsername());
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request,
                                                 @AuthenticationPrincipal UserDetails userDetails,
                                                 HttpServletRequest httpRequest) {
        UserAccount user = new UserAccount(request.fullName(), request.email(), request.password());
        List<RoleName> roles = request.roles().stream()
                .map(role -> RoleName.fromValue(role.replace("ROLE_", "")))
                .collect(Collectors.toList());
        UserAccount saved = userManagementPort.register(user, roles);

        // Un solo rol
        Set<String> authorities = Set.of(saved.getRol());

        String token = jwtTokenProvider.generateToken(saved.getEmail(), authorities);
        if (userDetails != null) {
            activityMonitoringService.recordAction(
                    "REGISTER",
                    userDetails.getUsername(),
                    userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()),
                    httpRequest,
                    200,
                    "Usuario creado: " + saved.getEmail()
            );
        }
        return ResponseEntity.ok(new AuthResponseDTO(token, saved.getEmail(), authorities));
    }



}
