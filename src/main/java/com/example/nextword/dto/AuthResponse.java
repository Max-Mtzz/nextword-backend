package com.example.nextword.dto;

public class AuthResponse {
    private String token;
    private String email;
    private String rol;
    private Long id;

    public AuthResponse(String token, String email, String rol, Long id) {
        this.token = token;
        this.email = email;
        this.rol = rol;
        this.id = id;
    }

    // Getters
    public String getToken() { return token; }
    public String getEmail() { return email; }
    public String getRol() { return rol; }
    public Long getId() { return id; }
}