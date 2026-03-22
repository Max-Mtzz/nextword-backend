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

    public List<Horario> obtenerPorCurso(Long cursoId) {
        return horarioRepository.findByCursoId(cursoId);
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