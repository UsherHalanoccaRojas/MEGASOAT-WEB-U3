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
    }
