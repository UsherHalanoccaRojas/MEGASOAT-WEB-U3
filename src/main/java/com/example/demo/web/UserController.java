package com.example.demo.web;

import com.example.demo.application.port.in.UserManagementPort;
import com.example.demo.domain.model.RoleName;
import com.example.demo.domain.model.UserAccount;
import com.example.demo.web.dto.RoleAssignmentRequestDTO;
import com.example.demo.web.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserManagementPort userManagementPort;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserManagementPort userManagementPort, PasswordEncoder passwordEncoder) {
        this.userManagementPort = userManagementPort;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<List<UserDTO>> listUsers() {
        List<UserDTO> users = userManagementPort.findAllUsers()
                .stream()
                .map(u -> new UserDTO(u.getFullName(), u.getEmail(), u.getRol(), u.isActive(), u.getAvatarUrl()))
                .toList();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/assign-role")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<UserAccount> assignRole(@RequestBody RoleAssignmentRequestDTO request) {
        RoleName roleName = RoleName.fromValue(request.getRole().replace("ROLE_", ""));
        UserAccount updated = userManagementPort.assignRole(request.getEmail(), roleName);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/reset-password")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<Void> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String newPassword = body.get("newPassword");
        if (email == null || newPassword == null || newPassword.length() < 6)
            return ResponseEntity.badRequest().build();
        userManagementPort.findByEmail(email).ifPresent(user -> {
            user.setPassword(passwordEncoder.encode(newPassword));
            userManagementPort.updateUser(user);
        });
        return ResponseEntity.ok().build();
    }

    @PostMapping("/toggle-active")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<Void> toggleActive(@RequestBody Map<String, Object> body) {
        String email = (String) body.get("email");
        Boolean active = (Boolean) body.get("active");
        if (email == null || active == null) return ResponseEntity.badRequest().build();
        userManagementPort.findByEmail(email).ifPresent(user -> {
            user.setActive(active);
            userManagementPort.updateUser(user);
        });
        return ResponseEntity.ok().build();
    }

    // ── PERFIL DEL USUARIO AUTENTICADO ──────────────────────────

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return userManagementPort.findByEmail(userDetails.getUsername())
                .map(u -> {
                    Map<String, Object> profile = new HashMap<>();
                    profile.put("id",        u.getId());
                    profile.put("fullName",  u.getFullName());
                    profile.put("email",     u.getEmail());
                    profile.put("username",  u.getUsername());
                    profile.put("rol",       u.getRol());
                    profile.put("avatarUrl", u.getAvatarUrl());
                    return ResponseEntity.ok(profile);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<Map<String, Object>> updateMyProfile(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        return userManagementPort.findByEmail(userDetails.getUsername())
                .map(u -> {
                    String fullName = body.get("fullName");
                    if (fullName != null && !fullName.isBlank()) u.setFullName(fullName.trim());
                    userManagementPort.updateUser(u);
                    Map<String, Object> res = new HashMap<>();
                    res.put("fullName", u.getFullName());
                    res.put("email",    u.getEmail());
                    res.put("rol",      u.getRol());
                    return ResponseEntity.ok(res);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me/password")
    public ResponseEntity<Map<String, String>> changeMyPassword(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        String currentPassword = body.get("currentPassword");
        String newPassword     = body.get("newPassword");
        if (newPassword == null || newPassword.length() < 6)
            return ResponseEntity.badRequest().body(Map.of("error", "La contraseña debe tener al menos 6 caracteres"));
        return userManagementPort.findByEmail(userDetails.getUsername())
                .map(u -> {
                    if (!passwordEncoder.matches(currentPassword, u.getPassword()))
                        return ResponseEntity.status(401).<Map<String, String>>body(Map.of("error", "Contraseña actual incorrecta"));
                    u.setPassword(passwordEncoder.encode(newPassword));
                    userManagementPort.updateUser(u);
                    return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me/avatar")
    public ResponseEntity<Map<String, String>> updateMyAvatar(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        String avatarUrl = body.get("avatarUrl");
        if (avatarUrl == null || avatarUrl.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "Avatar inválido"));
        return userManagementPort.findByEmail(userDetails.getUsername())
                .map(u -> {
                    u.setAvatarUrl(avatarUrl);
                    userManagementPort.updateUser(u);
                    return ResponseEntity.ok(Map.of("avatarUrl", avatarUrl));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
