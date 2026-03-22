package com.example.nextword.model;

import jakarta.persistence.*;
import lombok.Data;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.ZonedDateTime;
import java.util.List;
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

    // Importa estas dos librerías arriba si no las tienes:
    // import java.util.List;
    // import com.fasterxml.jackson.annotation.JsonIgnore;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Súper importante para que al pedir los cursos no se haga un ciclo infinito
    private List<Horario> horarios;
}