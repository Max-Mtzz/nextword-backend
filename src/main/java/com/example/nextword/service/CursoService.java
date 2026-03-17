package com.example.nextword.service;

import com.example.nextword.model.Curso;
import com.example.nextword.repository.CursoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    public Curso crearCurso(Curso curso) {
        // Validación: Evitar cursos con nombres duplicados
        if (cursoRepository.existsByNombre(curso.getNombre())) {
            throw new IllegalArgumentException("Error: Ya existe un curso con este nombre.");
        }
        return cursoRepository.save(curso);
    }

    public List<Curso> obtenerTodos() {
        return cursoRepository.findAll();
    }

    public Optional<Curso> obtenerPorId(Long id) {
        return cursoRepository.findById(id);
    }

    public void eliminarCurso(Long id) {
        if (!cursoRepository.existsById(id)) {
            throw new IllegalArgumentException("Error: El curso que intentas eliminar no existe.");
        }
        cursoRepository.deleteById(id);
    }
}