package com.example.nextword.repository;

import com.example.nextword.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    
    // Función mágica de Spring para saber si un curso ya existe
    boolean existsByNombre(String nombre);
}