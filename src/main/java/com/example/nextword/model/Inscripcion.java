package com.example.nextword.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inscripciones")
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_alumno", nullable = false)
    private Usuario alumno;

    // Como es 1 a 1 (por tu restricción UNIQUE), Spring Boot lo entiende mejor así:
    @OneToOne 
    @JoinColumn(name = "id_horario", nullable = false, unique = true)
    private Horario horario;

    @Column(name = "modalidad_seleccionada", length = 20, nullable = false)
    private String modalidadSeleccionada; // 'presencial' o 'virtual'

    @Column(name = "datos_codigo_qr", length = 1000)
    private String datosCodigoQr;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
}