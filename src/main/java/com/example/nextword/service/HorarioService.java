package com.example.nextword.service;

import com.example.nextword.model.Horario;
import com.example.nextword.repository.HorarioRepository;
import org.springframework.stereotype.Service;

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

    // --- ¡MÉTODO MODIFICADO PARA DIVIDIR EN HORAS! ---
    public List<Horario> guardarHorario(Horario horarioPeticion) {
        if (horarioPeticion.getFechaHoraFin().isBefore(horarioPeticion.getFechaHoraClase())) {
            throw new IllegalArgumentException("La hora de fin no puede ser antes que la de inicio.");
        }

        List<Horario> bloquesGenerados = new ArrayList<>();
        LocalDateTime horaActual = horarioPeticion.getFechaHoraClase();
        LocalDateTime horaFinTotal = horarioPeticion.getFechaHoraFin();

        // Ciclo para dividir el rango en bloques de 1 hora
        while (horaActual.isBefore(horaFinTotal)) {
            LocalDateTime finDeBloque = horaActual.plusHours(1);

            // Evitamos pasarnos de la hora final por si ponen medias horas
            if (finDeBloque.isAfter(horaFinTotal)) {
                finDeBloque = horaFinTotal;
            }

            // Creamos un nuevo objeto por cada bloque de hora
            Horario bloque = new Horario();
            bloque.setCurso(horarioPeticion.getCurso());
            bloque.setDocente(horarioPeticion.getDocente());
            bloque.setEstado(horarioPeticion.getEstado() != null ? horarioPeticion.getEstado() : "disponible");
            bloque.setFechaHoraClase(horaActual);
            bloque.setFechaHoraFin(finDeBloque);

            bloquesGenerados.add(bloque);

            // Avanzamos a la siguiente hora
            horaActual = finDeBloque;
        }

        // Guardamos todos los bloques de golpe en la base de datos
        return horarioRepository.saveAll(bloquesGenerados);
    }

    public List<Horario> obtenerTodos() {
        return horarioRepository.findAll();
    }

    public List<Horario> obtenerPorCurso(Long cursoId) {
        return horarioRepository.findByCursoId(cursoId);
    }

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