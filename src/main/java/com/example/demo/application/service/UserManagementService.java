    package com.example.demo.application.service;

    import com.example.demo.application.exception.DuplicateResourceException;
    import com.example.demo.application.exception.ResourceNotFoundException;
    import com.example.demo.application.port.in.UserManagementPort;
    import com.example.demo.domain.model.RoleName;
    import com.example.demo.domain.model.UserAccount;
    import com.example.demo.infrastructure.persistence.UserRepository;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.List;
    import java.util.Optional;

    @Service
    @Transactional
    public class UserManagementService implements UserManagementPort {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        public UserManagementService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
        }

@Override
public UserAccount register(UserAccount user, List<RoleName> roles) {
    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
        throw new DuplicateResourceException("Usuario ya registrado: " + user.getEmail());
    }
    if (roles == null || roles.isEmpty()) {
        throw new IllegalArgumentException("Se debe asignar al menos un rol al usuario");
    }

    // Encriptar contraseña
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    // Asignar primer rol como texto
    user.setRol(roles.get(0).name());

    return userRepository.save(user);
 }

        @Override
        public Optional<UserAccount> findByEmail(String email) {
            return userRepository.findByEmail(email);
        }

        @Override
        public List<UserAccount> findAllUsers() {
            return userRepository.findAll();
        }

@Override
public UserAccount assignRole(String email, RoleName role) {
    UserAccount user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + email));
    user.setRol(role.name());
    return userRepository.save(user);
}

@Override
public UserAccount updateUser(UserAccount user) {
    return userRepository.save(user);
}

@Override
public void resetPassword(String email, String newPassword) {
    if (newPassword == null || newPassword.length() < 6) {
        throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
    }
    UserAccount user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + email));
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
}

@Override
public void toggleActive(String email, boolean active) {
    UserAccount user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + email));
    user.setActive(active);
    userRepository.save(user);
}

@Override
public UserAccount updateProfile(String email, String fullName) {
    UserAccount user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + email));
    if (fullName != null && !fullName.isBlank()) {
        user.setFullName(fullName.trim());
    }
    return userRepository.save(user);
}

@Override
public void changePassword(String email, String currentPassword, String newPassword) {
    if (newPassword == null || newPassword.length() < 6) {
        throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
    }
    UserAccount user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + email));
    if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
        throw new IllegalArgumentException("Contraseña actual incorrecta");
    }
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
}

@Override
public UserAccount updateAvatar(String email, String avatarUrl) {
    if (avatarUrl == null || avatarUrl.isBlank()) {
        throw new IllegalArgumentException("Avatar inválido");
    }
    UserAccount user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + email));
    user.setAvatarUrl(avatarUrl);
    return userRepository.save(user);
}
    }
