package com.example.nextword.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // 1. Desactivamos CSRF porque usaremos JWT (que ya es seguro)
            .cors(cors -> cors.configure(http)) // 2. Activamos tu CorsConfig
            .authorizeHttpRequests(auth -> auth
                // 3. ¡LAS RUTAS PÚBLICAS! Cualquiera puede registrarse o hacer login
                .requestMatchers("/api/usuarios/registro", "/api/usuarios/login").permitAll()
                // 4. Cualquier otra ruta (crear cursos, inscribirse, etc.) requiere estar autenticado
                .anyRequest().authenticated()
            )
            // 5. Le decimos a Spring que no guarde sesiones en memoria (porque el JWT trae toda la info)
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    // Herramienta para encriptar las contraseñas en la base de datos (NUNCA guardar texto plano)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}