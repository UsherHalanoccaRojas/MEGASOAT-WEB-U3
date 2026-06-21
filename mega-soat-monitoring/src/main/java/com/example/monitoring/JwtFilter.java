package com.example.monitoring;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

// Filtro JWT: intercepta cada petición HTTP y valida el token Bearer.
// Si el token es válido, establece la identidad del usuario en Spring Security.
// Se ejecuta UNA sola vez por petición (hereda de OncePerRequestFilter).
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null) {
            try {
                // Valida la firma del token con la clave secreta
                byte[] keyBytes = padTo32(secret.getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes))
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                // Extrae usuario y roles del token
                String username = claims.getSubject();
                String rolesStr = claims.get("roles", String.class);
                List<SimpleGrantedAuthority> authorities = rolesStr != null
                        ? List.of(rolesStr.split(",")).stream()
                              .map(String::trim)
                              .filter(r -> !r.isBlank())
                              .map(SimpleGrantedAuthority::new)
                              .toList()
                        : List.of();

                // Registra la identidad en el contexto de seguridad de Spring
                var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception ignored) {
                // Token inválido o expirado → la petición continúa sin autenticación
            }
        }

        chain.doFilter(request, response); // Pasa al siguiente filtro o al controlador
    }

    // Extrae el token del encabezado "Authorization: Bearer <token>"
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    // Asegura que la clave tenga al menos 32 bytes (256 bits) para HMAC-SHA256
    private byte[] padTo32(byte[] key) {
        if (key.length >= 32) return key;
        byte[] padded = new byte[32];
        System.arraycopy(key, 0, padded, 0, key.length);
        return padded;
    }
}
