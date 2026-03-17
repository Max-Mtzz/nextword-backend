package com.example.nextword.repository;

import com.example.nextword.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HorarioRepository extends JpaRepository<Horario, Long> {
    // Esto servirá para cuando el Admin entre a un curso y quiera ver solo SUS horarios
    List<Horario> findByCursoId(Long cursoId);
}