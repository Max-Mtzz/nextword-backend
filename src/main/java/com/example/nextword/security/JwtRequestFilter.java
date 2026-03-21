package com.example.nextword.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // Inyectamos nuestro generador/lector de tokens
    public JwtRequestFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 1. Buscamos el token en el Header "Authorization"
        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        // 2. Si el header existe y empieza con "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Quitamos la palabra "Bearer " para dejar solo el token
            try {
                email = jwtUtil.extractUsername(jwt); // Sacamos el correo del token
            } catch (Exception e) {
                System.out.println("Token inválido o expirado");
            }
        }

        // 3. Si encontramos un correo y el usuario aún no está autenticado en este proceso
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 4. Validamos que el token siga vivo y pertenezca a ese correo
            if (jwtUtil.isTokenValid(jwt, email)) {
                
                // 5. ¡Le decimos a Spring Security que el usuario es válido!
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        email, null, new ArrayList<>()); // (El ArrayList vacío es para los Roles, lo usaremos después)

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        
        // 6. Continúa su camino hacia el Controlador
        chain.doFilter(request, response);
    }
}