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

    // Añade este método en tu CursoService.java
    public Curso actualizarCurso(Long id, Curso detallesCurso) {
        Curso cursoExistente = cursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El curso que intentas actualizar no existe."));

        // Validar que el nuevo nombre no choque con otro curso existente
        if (!cursoExistente.getNombre().equals(detallesCurso.getNombre()) && 
            cursoRepository.existsByNombre(detallesCurso.getNombre())) {
            throw new IllegalArgumentException("Error: Ya existe otro curso con este nombre.");
        }

        // Actualizamos el nombre
        cursoExistente.setNombre(detallesCurso.getNombre());
        
        // Solo actualizamos la URL de la imagen si nos mandan una nueva
        if (detallesCurso.getUrlImagen() != null) {
            cursoExistente.setUrlImagen(detallesCurso.getUrlImagen());
        }

        return cursoRepository.save(cursoExistente);
    }
}