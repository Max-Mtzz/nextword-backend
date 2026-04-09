package com.example.nextword.service;

import com.example.nextword.model.Curso;
import com.example.nextword.repository.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- Importación agregada

import java.util.List;
import java.util.Optional;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    @Transactional // <-- Agregado
    public Curso crearCurso(Curso curso) {
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

    @Transactional // <-- Agregado
    public void eliminarCurso(Long id) {
        if (!cursoRepository.existsById(id)) {
            throw new IllegalArgumentException("Error: El curso que intentas eliminar no existe.");
        }
        cursoRepository.deleteById(id);
    }

    @Transactional // <-- Agregado
    public Curso actualizarCurso(Long id, Curso detallesCurso) {
        Curso cursoExistente = cursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El curso que intentas actualizar no existe."));

        if (!cursoExistente.getNombre().equals(detallesCurso.getNombre()) && 
            cursoRepository.existsByNombre(detallesCurso.getNombre())) {
            throw new IllegalArgumentException("Error: Ya existe otro curso con este nombre.");
        }

        cursoExistente.setNombre(detallesCurso.getNombre());
        
        if (detallesCurso.getUrlImagen() != null) {
            cursoExistente.setUrlImagen(detallesCurso.getUrlImagen());
        }

        return cursoRepository.save(cursoExistente);
    }
}