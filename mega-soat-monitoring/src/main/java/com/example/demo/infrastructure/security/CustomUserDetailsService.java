package com.example.demo.infrastructure.security;

import com.example.demo.domain.model.UserAccount;
import com.example.demo.infrastructure.persistence.UserRepository;
import java.util.Set;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserAccount account = userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

    // Un solo rol como autoridad
    String rol = account.getRol() == null ? "USER" : account.getRol().toUpperCase();
    if (!rol.startsWith("ROLE_")) rol = "ROLE_" + rol;
    Set<SimpleGrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority(rol));

    return new User(
            account.getEmail(),
            account.getPassword(),
            account.isActive(),
            true,
            true,
            true,
            authorities
    );
}

}

