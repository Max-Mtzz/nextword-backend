package com.example.nextword.service;

import com.example.nextword.model.Horario;
import com.example.nextword.model.Inscripcion;
import com.example.nextword.model.Usuario;
import com.example.nextword.repository.HorarioRepository;
import com.example.nextword.repository.InscripcionRepository;
import com.example.nextword.repository.UsuarioRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final HorarioRepository horarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final JavaMailSender mailSender; 

    public InscripcionService(InscripcionRepository inscripcionRepository, 
                              HorarioRepository horarioRepository,
                              UsuarioRepository usuarioRepository,
                              JavaMailSender mailSender) {
        this.inscripcionRepository = inscripcionRepository;
        this.horarioRepository = horarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.mailSender = mailSender;
    }

    // --- 1. INSCRIBIR ---
    public Inscripcion inscribirAlumno(Inscripcion inscripcion) {
        // Buscar el horario completo en BD
        Horario horario = horarioRepository.findById(inscripcion.getHorario().getId())
                .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado"));

        // Buscar al alumno completo en BD para que no salga null
        Usuario alumno = usuarioRepository.findById(inscripcion.getAlumno().getId())
                .orElseThrow(() -> new IllegalArgumentException("Alumno no encontrado"));
        inscripcion.setAlumno(alumno);

        // Validar que no esté ya ocupado
        if (horario.getEstado().equals("ocupado")) {
            throw new IllegalStateException("Esta clase ya está ocupada por otro alumno.");
        }

        horario.setEstado("ocupado");
        horarioRepository.save(horario);

        inscripcion.setHorario(horario);
        inscripcion.setModalidadSeleccionada(inscripcion.getModalidadSeleccionada().toLowerCase());

        // --- ¡NUEVA LÓGICA DEL QR CON NOMBRES REALES! ---
        if (inscripcion.getModalidadSeleccionada().equals("presencial")) {
            String nombreCurso = horario.getCurso().getNombre();
            String nombreDocente = horario.getDocente().getFullName();
            String nombreAlumno = alumno.getFullName();
            String fechaClase = horario.getFechaHoraClase().toString();

            // Usamos \n para que al escanearlo salga como una lista hacia abajo
            String datosQR = String.format(
                    "Curso: %s\nDocente: %s\nAlumno: %s\nFecha: %s\nModalidad: Presencial",
                    nombreCurso, nombreDocente, nombreAlumno, fechaClase
            );
            
            inscripcion.setDatosCodigoQr(datosQR);
        } else {
            inscripcion.setDatosCodigoQr(null);
        }
        // ------------------------------------------------

        Inscripcion nuevaInscripcion = inscripcionRepository.save(inscripcion);
        Inscripcion inscripcionCompleta = inscripcionRepository.findById(nuevaInscripcion.getId()).orElse(nuevaInscripcion);

        enviarCorreoAlDocente(inscripcionCompleta);

        return inscripcionCompleta;
    }

    // --- 2. CANCELAR INSCRIPCIÓN ---
    public void cancelarInscripcion(Long idInscripcion) {
        Inscripcion inscripcion = inscripcionRepository.findById(idInscripcion)
                .orElseThrow(() -> new IllegalArgumentException("Inscripción no encontrada"));

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaClase = inscripcion.getHorario().getFechaHoraClase();
        long minutosDesdeInscripcion = ChronoUnit.MINUTES.between(inscripcion.getFechaCreacion(), ahora);

        if (ahora.isBefore(fechaClase) && ahora.plusHours(24).isAfter(fechaClase)) {
            if (minutosDesdeInscripcion > 30) {
                throw new IllegalStateException("No puedes cancelar la clase con menos de 24 horas de anticipación.");
            }
        }

        Horario horario = inscripcion.getHorario();
        horario.setEstado("disponible");
        horarioRepository.save(horario);

        inscripcionRepository.delete(inscripcion);
    }

    public Inscripcion obtenerInscripcionPorId(Long id) {
        return inscripcionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inscripción no encontrada"));
    }

    // --- OBTENER INSCRIPCIONES POR ALUMNO ---
    public List<Inscripcion> obtenerPorAlumno(Long alumnoId) {
        return inscripcionRepository.findByAlumnoId(alumnoId);
    }

    // --- 3. MÉTODO PARA ENVIAR CORREOS ---
    private void enviarCorreoAlDocente(Inscripcion inscripcion) {
        try {
            String correoDocente = inscripcion.getHorario().getDocente().getEmail();
            String correoAlumno = inscripcion.getAlumno().getEmail();
            String nombreDocente = inscripcion.getHorario().getDocente().getFullName();
            String nombreAlumno = inscripcion.getAlumno().getFullName(); 
            String nombreCurso = inscripcion.getHorario().getCurso().getNombre();
            String fechaClase = inscripcion.getHorario().getFechaHoraClase().toString();
            String modalidad = inscripcion.getModalidadSeleccionada().toUpperCase();

            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom("max.mtz.roman@gmail.com", "Equipo Nextword"); 
            helper.setTo(correoDocente);
            helper.setSubject("¡Nuevo alumno inscrito en tu clase de " + nombreCurso + "!");
            helper.setText("Hola Profesor(a) " + nombreDocente + ",\n\n"
                    + "Te informamos que el alumno " + nombreAlumno + " se ha inscrito a tu clase.\n\n"
                    + "Correo del alumno: "+ correoAlumno +"\n"
                    + "Detalles de la clase:\n"
                    + "- Curso: " + nombreCurso + "\n"
                    + "- Fecha y Hora: " + fechaClase + "\n"
                    + "- Modalidad: " + modalidad + "\n\n"
                    + "Por favor, ponte en contacto con el alumno para coordinar los detalles.\n\n"
                    + "Saludos,\n"
                    + "El equipo de Nextword");

            mailSender.send(mensaje);
            System.out.println("Correo enviado exitosamente al docente: " + correoDocente);

        } catch (Exception e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
        }
    }
}