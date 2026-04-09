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

    // 1. CREAR HORARIOS (¡MODIFICADO!)
    @PostMapping
    public ResponseEntity<List<Horario>> crearHorario(@RequestBody Horario horario) {
        // Ahora recibe la lista de todos los bloques que se generaron
        List<Horario> nuevosHorarios = horarioService.guardarHorario(horario);
        return ResponseEntity.ok(nuevosHorarios);
    }

    // 2. OBTENER POR CURSO
    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<Horario>> listarPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(horarioService.obtenerPorCurso(cursoId));
    }

    // 3. EDITAR HORARIO
    @PutMapping("/{id}")
    public ResponseEntity<Horario> actualizarHorario(@PathVariable Long id, @RequestBody Horario horarioActualizado) {
        Horario horarioGuardado = horarioService.actualizarHorario(id, horarioActualizado);
        return ResponseEntity.ok(horarioGuardado);
    }

    // 4. ELIMINAR HORARIO
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarHorario(@PathVariable Long id) {
        horarioService.eliminarHorario(id); 
        return ResponseEntity.ok("Horario eliminado exitosamente.");
    }

    // 5. OBTENER TODOS LOS HORARIOS
    @GetMapping
    public ResponseEntity<List<Horario>> listarTodos() {
        return ResponseEntity.ok(horarioService.obtenerTodos()); 
    }
}