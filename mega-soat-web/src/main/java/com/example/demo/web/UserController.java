package com.example.demo.web;

import com.example.demo.application.port.in.UserManagementPort;
import com.example.demo.domain.model.RoleName;
import com.example.demo.domain.model.UserAccount;
import com.example.demo.web.dto.BusinessDTOs.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserManagementPort userManagementPort;

    public UserController(UserManagementPort userManagementPort) {
        this.userManagementPort = userManagementPort;
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
        RoleName roleName = RoleName.fromValue(request.role().replace("ROLE_", ""));
        UserAccount updated = userManagementPort.assignRole(request.email(), roleName);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/reset-password")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<Void> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String newPassword = body.get("newPassword");
        try {
            userManagementPort.resetPassword(email, newPassword);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/toggle-active")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<Void> toggleActive(@RequestBody Map<String, Object> body) {
        String email = (String) body.get("email");
        Boolean active = (Boolean) body.get("active");
        if (email == null || active == null) return ResponseEntity.badRequest().build();
        try {
            userManagementPort.toggleActive(email, active);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
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
        try {
            UserAccount u = userManagementPort.updateProfile(userDetails.getUsername(), body.get("fullName"));
            Map<String, Object> res = new HashMap<>();
            res.put("fullName", u.getFullName());
            res.put("email",    u.getEmail());
            res.put("rol",      u.getRol());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/me/password")
    public ResponseEntity<Map<String, String>> changeMyPassword(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            userManagementPort.changePassword(
                    userDetails.getUsername(),
                    body.get("currentPassword"),
                    body.get("newPassword")
            );
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
        } catch (IllegalArgumentException e) {
            if ("Contraseña actual incorrecta".equals(e.getMessage())) {
                return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/me/avatar")
    public ResponseEntity<Map<String, String>> updateMyAvatar(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            UserAccount u = userManagementPort.updateAvatar(userDetails.getUsername(), body.get("avatarUrl"));
            return ResponseEntity.ok(Map.of("avatarUrl", u.getAvatarUrl()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
