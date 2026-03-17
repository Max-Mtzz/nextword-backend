package com.example.nextword.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inscripciones")
public class Inscripcion {

    @Id // Le asignamos un identificador para que la BD pueda saber que este campo es el id
    @GeneratedValue(strategy = GenerationType.IDENTITY)// Auto generamos el Identificador
    private Long id;// Variable de ID

    @ManyToOne// Relacion de muchos a uno
    @JoinColumn(name = "id_alumno", nullable = false)
    private Usuario alumno;

    // Como es 1 a 1 (por tu restricción UNIQUE), Spring Boot lo entiende mejor así:
    @OneToOne 
    @JoinColumn(name = "id_horario", nullable = false, unique = true)
    private Horario horario;

    @Column(name = "modalidad_seleccionada", length = 20, nullable = false)
    private String modalidadSeleccionada; // 'presencial' o 'virtual'

    @Column(name = "datos_codigo_qr", length = 1000)
    private String datosCodigoQr;// Aquí guardaremos los datos del código QR para que el móvil lo genere

    @CreationTimestamp// Guardamos la fecha de creación al usuario
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
}