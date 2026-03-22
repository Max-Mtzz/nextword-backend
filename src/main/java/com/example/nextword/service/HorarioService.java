package com.example.nextword.service;

import com.example.nextword.model.Horario;
import com.example.nextword.repository.HorarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class HorarioService {

    private final HorarioRepository horarioRepository;

    public HorarioService(HorarioRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    public Horario guardarHorario(Horario horario) {
        // Aquí podrías validar que la fecha de fin no sea antes que la de inicio
        if (horario.getFechaHoraFin().isBefore(horario.getFechaHoraClase())) {
            throw new IllegalArgumentException("La hora de fin no puede ser antes que la de inicio.");
        }
        return horarioRepository.save(horario);
    }

    // --- ¡NUEVO MÉTODO QUE FALTABA! ---
    public List<Horario> obtenerTodos() {
        return horarioRepository.findAll();
    }
    // ----------------------------------

    public List<Horario> obtenerPorCurso(Long cursoId) {
        return horarioRepository.findByCursoId(cursoId);
    }

    public Horario actualizarHorario(Long id, Horario horarioActualizado) {
        // 1. Buscamos que el horario realmente exista en la BD
        Horario horarioExistente = horarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("El horario no existe."));

        // 2. Validamos la lógica de las horas
        if (horarioActualizado.getFechaHoraFin().isBefore(horarioActualizado.getFechaHoraClase())) {
            throw new IllegalArgumentException("La hora de fin no puede ser antes que la de inicio.");
        }

        // 3. Actualizamos los campos con los datos que llegaron de React
        horarioExistente.setFechaHoraClase(horarioActualizado.getFechaHoraClase());
        horarioExistente.setFechaHoraFin(horarioActualizado.getFechaHoraFin());
        horarioExistente.setEstado(horarioActualizado.getEstado());
        horarioExistente.setDocente(horarioActualizado.getDocente());
        
        // 4. Guardamos los cambios
        return horarioRepository.save(horarioExistente);
    }
    
    public void eliminarHorario(Long id) {
        Horario horario = horarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("El horario no existe."));

        LocalDateTime ahora = LocalDateTime.now();
        long horasDeAnticipacion = ChronoUnit.HOURS.between(ahora, horario.getFechaHoraClase());
        long minutosDesdeCreacion = ChronoUnit.MINUTES.between(horario.getFechaActualizacion(), ahora);

        // Solo aplicamos la regla si la clase es en el FUTURO y faltan menos de 24 hrs
        if (horasDeAnticipacion >= 0 && horasDeAnticipacion < 24) {
            // Período de gracia: Si lo creó o editó hace menos de 30 mins, le permitimos borrar
            if (minutosDesdeCreacion > 30) {
                throw new IllegalArgumentException("No es posible cancelar. Faltan menos de 24 horas para la clase.");
            }
        } 
        
        // Eliminamos la validación de (horasDeAnticipacion < 0) para que el Admin pueda borrar históricos
        horarioRepository.deleteById(id);
    }
}