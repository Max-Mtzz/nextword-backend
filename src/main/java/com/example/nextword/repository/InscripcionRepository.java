package com.example.nextword.repository;

import com.example.nextword.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    
    // Método para encontrar todas las inscripciones de un alumno específico
    List<Inscripcion> findByAlumnoId(Long alumnoId);
}