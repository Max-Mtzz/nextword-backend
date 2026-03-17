package com.example.nextword.controller;

import com.example.nextword.model.Horario;
import com.example.nextword.service.HorarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    private final HorarioService horarioService;

    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    // 1. CREAR HORARIO (POST: http://localhost:8080/api/horarios)
    @PostMapping
    public ResponseEntity<?> crearHorario(@RequestBody Horario horario) {
        try {
            Horario nuevoHorario = horarioService.guardarHorario(horario);
            return ResponseEntity.ok(nuevoHorario);
        } catch (IllegalArgumentException e) {
            // Atrapa errores de fechas incoherentes
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. OBTENER HORARIOS POR CURSO (GET: http://localhost:8080/api/horarios/curso/1)
    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<Horario>> listarPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(horarioService.obtenerPorCurso(cursoId));
    }

    // 3. ELIMINAR HORARIO (DELETE: http://localhost:8080/api/horarios/1)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarHorario(@PathVariable Long id) {
        try {
            horarioService.eliminarHorario(id); // Llama a la regla de las 24 horas
            return ResponseEntity.ok("Horario eliminado exitosamente.");
        } catch (IllegalArgumentException e) {
            // ¡Aquí es donde atrapamos la regla de las 24 horas del DFR!
            // Le mandamos el error al Frontend para que muestre un pop-up
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ocurrió un error al intentar eliminar el horario.");
        }
    }
}