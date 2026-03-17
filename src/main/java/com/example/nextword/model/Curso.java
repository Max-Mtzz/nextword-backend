package com.example.nextword.model;

import jakarta.persistence.*;
import lombok.Data;

import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
@Data
@Entity
@Table(name = "cursos")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, unique = true, length = 255)
    private String nombre;

    @Column(name = "url_imagen", nullable = false, length = 1000)
    private String urlImagen;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private ZonedDateTime fechaCreacion;

}