package com.example.nextword.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Esta es la firma secreta de tu aplicación. ¡Si alguien la roba, puede crear tokens falsos!
    // En producción, esto se guarda en el application.properties, pero por ahora la dejaremos aquí.
    private static final String SECRET_KEY = "EstaEsUnaClaveSuperSecretaParaNextwordQueNadieDebeSaber2026";
    
    // Convierte el texto secreto en una llave criptográfica
    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // 1. Crear el Token (El gafete)
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // A quién le pertenece (usaremos el correo)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Fecha de creación
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Expira en 10 horas
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // Lo firmamos
                .compact();
    }

    // 2. Extraer el correo del Token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 3. Validar si el Token aún sirve y pertenece al usuario
    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }
}