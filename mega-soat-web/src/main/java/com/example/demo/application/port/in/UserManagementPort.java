package com.example.demo.application.port.in;

import com.example.demo.domain.model.RoleName;
import com.example.demo.domain.model.UserAccount;

import java.util.List;
import java.util.Optional;

public interface UserManagementPort {

    UserAccount register(UserAccount user, List<RoleName> roles);

    Optional<UserAccount> findByEmail(String email);

    List<UserAccount> findAllUsers();

    UserAccount assignRole(String email, RoleName role);

    UserAccount updateUser(UserAccount user);

    void resetPassword(String email, String newPassword);

    void toggleActive(String email, boolean active);

    UserAccount updateProfile(String email, String fullName);

    void changePassword(String email, String currentPassword, String newPassword);

    UserAccount updateAvatar(String email, String avatarUrl);
}
