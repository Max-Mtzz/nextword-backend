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

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    // POST: http://localhost:8080/api/cursos
    @PostMapping
    public ResponseEntity<?> crearCurso(@RequestBody Curso curso) {
        try {
            Curso nuevoCurso = cursoService.crearCurso(curso);
            return new ResponseEntity<>(nuevoCurso, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // GET: http://localhost:8080/api/cursos
    @GetMapping
    public ResponseEntity<List<Curso>> obtenerCursos() {
        return new ResponseEntity<>(cursoService.obtenerTodos(), HttpStatus.OK);
    }

    // GET: http://localhost:8080/api/cursos/1
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Curso> curso = cursoService.obtenerPorId(id);
        if (curso.isPresent()) {
            return new ResponseEntity<>(curso.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Curso no encontrado", HttpStatus.NOT_FOUND);
        }
    }

    // DELETE: http://localhost:8080/api/cursos/1
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCurso(@PathVariable Long id) {
        try {
            cursoService.eliminarCurso(id);
            return new ResponseEntity<>("Curso eliminado exitosamente", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}