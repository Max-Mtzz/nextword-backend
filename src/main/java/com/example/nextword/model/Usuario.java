package com.example.nextword.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "perfiles") // Le dice a Spring que busque la tabla "perfiles"
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Como se llama "id" en ambos lados, no necesita @Column

    @Column(name = "nombre_usuario")
    private String username;

    @Column(name = "correo")
    private String email;

    @Column(name = "contrasena")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "rol")
    private String role;

    @Column(name = "nombre_completo")
    private String fullName;

    @Column(name = "genero")
    private String gender;

    @Column(name = "fecha_nacimiento")
    private LocalDate birthDate;

    @Column(name = "telefono_principal")
    private String primaryPhone;

    @Column(name = "telefono_emergencia")
    private String emergencyPhone;

    @Column(name = "fecha_creacion", updatable = false)// aquí guardamos la fecha de creación del usuario
    private LocalDateTime createdAt;

    @Column(name = "fecha_actualizacion")// aquí guardamos la fecha de creación del usuario
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}