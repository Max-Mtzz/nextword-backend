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

    // Constructor del controlador que conecta con el servicio de los horarios
    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    // 1. CREAR HORARIO
    @PostMapping
    public ResponseEntity<Horario> crearHorario(@RequestBody Horario horario) {
        Horario nuevoHorario = horarioService.guardarHorario(horario);
        return ResponseEntity.ok(nuevoHorario);
    }

    // 2. OBTENER POR CURSO
    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<Horario>> listarPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(horarioService.obtenerPorCurso(cursoId));
    }

    // 3. EDITAR HORARIO (¡NUEVO!)
    @PutMapping("/{id}")
    public ResponseEntity<Horario> actualizarHorario(@PathVariable Long id, @RequestBody Horario horarioActualizado) {
        // Asumiendo que tu HorarioService tiene un método para actualizar. 
        // Si no lo tiene, usualmente es buscar el ID, setear los nuevos datos y guardar.
        Horario horarioGuardado = horarioService.actualizarHorario(id, horarioActualizado);
        return ResponseEntity.ok(horarioGuardado);
    }

    // 4. ELIMINAR HORARIO
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarHorario(@PathVariable Long id) {
        // La regla de las 24 horas lanzará la excepción desde el Service/Trigger.
        horarioService.eliminarHorario(id); 
        return ResponseEntity.ok("Horario eliminado exitosamente.");
    }

    // 5. OBTENER TODOS LOS HORARIOS (NUEVO)
    @GetMapping
    public ResponseEntity<List<Horario>> listarTodos() {
        // Llama al método de tu servicio que devuelve todos los horarios
        return ResponseEntity.ok(horarioService.obtenerTodos()); 
    }
}