package com.example.nextword.model;

import jakarta.persistence.*;
import lombok.Data;

import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "horarios")
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Curso
    @ManyToOne
    @JoinColumn(name = "id_curso", nullable = false)
    private Curso curso;

    // Relación con el Docente (Perfil)
    @ManyToOne
    @JoinColumn(name = "id_docente", nullable = false)
    private Usuario docente;

    @Column(name = "fecha_hora_clase", nullable = false)
    private LocalDateTime fechaHoraClase;

    @Column(name = "fecha_hora_fin", nullable = false)
    private LocalDateTime fechaHoraFin;

    @Column(name = "estado", length = 20)
    private String estado = "disponible"; // Valor por defecto

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

}