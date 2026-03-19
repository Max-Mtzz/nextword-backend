package com.example.nextword.controller;

import com.example.nextword.model.Curso;
import com.example.nextword.service.CursoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoService cursoService;
    // Constructor del controlador que conecta con el servicio del curso
    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    // 1. CREAR CURSO
    @PostMapping
    public ResponseEntity<Curso> crearCurso(@RequestBody Curso curso) {
        Curso nuevoCurso = cursoService.crearCurso(curso);
        return new ResponseEntity<>(nuevoCurso, HttpStatus.CREATED);
    }

    // 2. OBTENER TODOS
    @GetMapping
    public ResponseEntity<List<Curso>> obtenerCursos() {
        return new ResponseEntity<>(cursoService.obtenerTodos(), HttpStatus.OK);
    }

    // 3. OBTENER POR ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Curso> curso = cursoService.obtenerPorId(id);
        if (curso.isPresent()) {
            return new ResponseEntity<>(curso.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Curso no encontrado", HttpStatus.NOT_FOUND);
        }
    }

    // 4. ELIMINAR CURSO
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCurso(@PathVariable Long id) {
        cursoService.eliminarCurso(id);
        return new ResponseEntity<>("Curso eliminado exitosamente", HttpStatus.OK);
    }
}