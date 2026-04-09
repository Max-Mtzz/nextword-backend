package com.example.nextword.service;

import com.example.nextword.model.Horario;
import com.example.nextword.repository.HorarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- Importación agregada

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class HorarioService {

    private final HorarioRepository horarioRepository;

    public HorarioService(HorarioRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    @Transactional // <-- Agregado
    public List<Horario> guardarHorario(Horario horarioPeticion) {
        if (horarioPeticion.getFechaHoraFin().isBefore(horarioPeticion.getFechaHoraClase())) {
            throw new IllegalArgumentException("La hora de fin no puede ser antes que la de inicio.");
        }

        List<Horario> bloquesGenerados = new ArrayList<>();
        LocalDateTime horaActual = horarioPeticion.getFechaHoraClase();
        LocalDateTime horaFinTotal = horarioPeticion.getFechaHoraFin();

        while (horaActual.isBefore(horaFinTotal)) {
            LocalDateTime finDeBloque = horaActual.plusHours(1);

            if (finDeBloque.isAfter(horaFinTotal)) {
                finDeBloque = horaFinTotal;
            }

            Horario bloque = new Horario();
            bloque.setCurso(horarioPeticion.getCurso());
            bloque.setDocente(horarioPeticion.getDocente());
            bloque.setEstado(horarioPeticion.getEstado() != null ? horarioPeticion.getEstado() : "disponible");
            bloque.setFechaHoraClase(horaActual);
            bloque.setFechaHoraFin(finDeBloque);

            bloquesGenerados.add(bloque);
            horaActual = finDeBloque;
        }

        return horarioRepository.saveAll(bloquesGenerados);
    }

    public List<Horario> obtenerTodos() {
        return horarioRepository.findAll();
    }

    public List<Horario> obtenerPorCurso(Long cursoId) {
        return horarioRepository.findByCursoId(cursoId);
    }

    @Transactional // <-- Agregado
    public Horario actualizarHorario(Long id, Horario horarioActualizado) {
        Horario horarioExistente = horarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("El horario no existe."));

        if (horarioActualizado.getFechaHoraFin().isBefore(horarioActualizado.getFechaHoraClase())) {
            throw new IllegalArgumentException("La hora de fin no puede ser antes que la de inicio.");
        }

        horarioExistente.setFechaHoraClase(horarioActualizado.getFechaHoraClase());
        horarioExistente.setFechaHoraFin(horarioActualizado.getFechaHoraFin());
        horarioExistente.setEstado(horarioActualizado.getEstado());
        horarioExistente.setDocente(horarioActualizado.getDocente());
        
        return horarioRepository.save(horarioExistente);
    }
    
    @Transactional // <-- Agregado
    public void eliminarHorario(Long id) {
        Horario horario = horarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("El horario no existe."));

        LocalDateTime ahora = LocalDateTime.now();
        long horasDeAnticipacion = ChronoUnit.HOURS.between(ahora, horario.getFechaHoraClase());
        long minutosDesdeCreacion = ChronoUnit.MINUTES.between(horario.getFechaActualizacion(), ahora);

        if (horasDeAnticipacion >= 0 && horasDeAnticipacion < 24) {
            if (minutosDesdeCreacion > 30) {
                throw new IllegalArgumentException("No es posible cancelar. Faltan menos de 24 horas para la clase.");
            }
        } 
        
        horarioRepository.deleteById(id);
    }
}