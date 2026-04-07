package com.example.nextword.controller;

import com.example.nextword.model.Inscripcion;
import com.example.nextword.service.InscripcionService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @PostMapping
    public ResponseEntity<Inscripcion> inscribir(@RequestBody Inscripcion inscripcion) {
        // ¡Cero try-catch! Si falla, el GlobalExceptionHandler lo atrapa
        Inscripcion confirmacion = inscripcionService.inscribirAlumno(inscripcion);
        return new ResponseEntity<>(confirmacion, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inscripcion> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(inscripcionService.obtenerInscripcionPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelarInscripcion(@PathVariable Long id) {
        inscripcionService.cancelarInscripcion(id);
        return ResponseEntity.ok("Inscripción cancelada exitosamente.");
    }

    // --- ¡NUEVO ENDPOINT! ---
    @GetMapping("/alumno/{alumnoId}")
    public ResponseEntity<List<Inscripcion>> obtenerPorAlumno(@PathVariable Long alumnoId) {
        return ResponseEntity.ok(inscripcionService.obtenerPorAlumno(alumnoId));
    }
}