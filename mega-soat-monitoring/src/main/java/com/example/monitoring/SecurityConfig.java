package com.example.monitoring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// Configuración de seguridad del microservicio.
// Define: qué rutas son públicas, cómo se valida el JWT, y el CORS.
@Configuration
@EnableMethodSecurity  // Habilita @PreAuthorize en los controladores
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Sin CSRF: API REST stateless no lo necesita
            .csrf(csrf -> csrf.disable())

            // CORS: permite peticiones desde la app principal (puerto 8080)
            .cors(cors -> cors.configurationSource(corsSource()))

            // Sin sesión HTTP: cada petición se autentica con el token JWT
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Reglas de acceso por ruta
            .authorizeHttpRequests(auth -> auth
                // internal-log es llamado por mega-soat-web (no tiene JWT propio)
                .requestMatchers("/api/admin/monitoring/internal-log").permitAll()
                // WebSocket handshake
                .requestMatchers("/ws/**").permitAll()
                // Todo lo demás requiere JWT válido (el rol se valida con @PreAuthorize)
                .anyRequest().authenticated()
            )

            // Registra el filtro JWT ANTES del filtro de autenticación estándar
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Permite solicitudes CORS desde la app principal en localhost:8080
    @Bean
    public CorsConfigurationSource corsSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:8080", "http://127.0.0.1:8080"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
