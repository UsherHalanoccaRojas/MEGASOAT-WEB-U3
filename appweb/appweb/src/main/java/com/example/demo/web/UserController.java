package com.example.demo.web;

import com.example.demo.application.port.in.UserManagementPort;
import com.example.demo.domain.model.RoleName;
import com.example.demo.domain.model.UserAccount;
import com.example.demo.web.dto.RoleAssignmentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserManagementPort userManagementPort;

    public UserController(UserManagementPort userManagementPort) {
        this.userManagementPort = userManagementPort;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<List<UserAccount>> listUsers() {
        return ResponseEntity.ok(userManagementPort.findAllUsers());
    }

    @PostMapping("/assign-role")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<UserAccount> assignRole(@RequestBody RoleAssignmentRequest request) {
        RoleName roleName = RoleName.fromValue(request.getRole().replace("ROLE_", ""));
        return ResponseEntity.ok(userManagementPort.assignRole(request.getEmail(), roleName));
    }
}
