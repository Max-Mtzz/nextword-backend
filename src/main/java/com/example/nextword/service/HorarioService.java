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

        // Si faltan menos de 24 horas (y la clase aún no ha pasado)
        if (horasDeAnticipacion >= 0 && horasDeAnticipacion < 24) {
            throw new IllegalArgumentException("No es posible cancelar. Faltan " + horasDeAnticipacion + " horas para la clase.");
        } 
        // Por si intentan borrar una clase del pasado
        else if (horasDeAnticipacion < 0) {
            throw new IllegalArgumentException("No puedes eliminar una clase que ya pasó o ya comenzó.");
        }

        horarioRepository.deleteById(id);
    }
}