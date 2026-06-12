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
}
