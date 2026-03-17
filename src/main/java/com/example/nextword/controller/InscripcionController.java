package com.example.nextword.controller;

import com.example.nextword.model.Inscripcion;
import com.example.nextword.service.InscripcionService;
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
    public ResponseEntity<?> inscribir(@RequestBody Inscripcion inscripcion) {
        try {
            Inscripcion confirmacion = inscripcionService.inscribirAlumno(inscripcion);
            return ResponseEntity.ok(confirmacion);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al inscribir: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(inscripcionService.obtenerInscripcionPorId(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelarInscripcion(@PathVariable Long id) {
        try {
            inscripcionService.cancelarInscripcion(id);
            return ResponseEntity.ok("Inscripción cancelada exitosamente. El horario vuelve a estar disponible.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al cancelar: " + e.getMessage());
        }
    }
}