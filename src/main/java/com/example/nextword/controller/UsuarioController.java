package com.example.nextword.controller;

import com.example.nextword.dto.AuthResponse;
import com.example.nextword.dto.LoginRequest;
import com.example.nextword.model.Usuario;
import com.example.nextword.service.EmailService;
import com.example.nextword.service.UsuarioService;
import com.example.nextword.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository; // ¡Agregamos el repositorio!
    private final PasswordEncoder passwordEncoder; // ¡Agregamos el encriptador!
    // Arriba con tus otras dependencias
    private final EmailService emailService;
    // Usamos esto para guardar temporalmente el token generado para cada correo
    private final Map<String, String> tokensRecuperacion = new java.util.concurrent.ConcurrentHashMap<>();

    // Actualizamos el constructor para que Spring nos inyecte las 3 cosas
    public UsuarioController(UsuarioService usuarioService,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder, EmailService emailService) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // 1. REGISTRAR
    @PostMapping("/registro")
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.registrarUsuario(usuario);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    // 2. OBTENER TODOS
    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodos() {
        return new ResponseEntity<>(usuarioService.obtenerTodosLosUsuarios(), HttpStatus.OK);
    }

    // 3. OBTENER POR ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.obtenerUsuarioPorId(id);
        if (usuario.isPresent()) {
            return new ResponseEntity<>(usuario.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
        }
    }

    // 4. ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return new ResponseEntity<>("Usuario eliminado exitosamente", HttpStatus.OK);
    }

    // 5. LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = usuarioService.login(request);
        return ResponseEntity.ok(response);
    }

    // 6. VALIDAR CORREO PARA RECUPERACIÓN
    @PostMapping("/validar-recuperacion")
    public ResponseEntity<?> validarCorreoRecuperacion(@RequestBody Map<String, String> request) {
        String correo = request.get("correo");

        // --- CHISMOSOS PARA LA CONSOLA ---
        System.out.println(">> [RECUPERACIÓN] Petición recibida para el correo: '" + correo + "'");

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(correo);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            System.out.println(">> [RECUPERACIÓN] Usuario ENCONTRADO. Su rol es: '" + usuario.getRole() + "'");

            if (usuario.getRole().equalsIgnoreCase("docente")) {
                System.out.println(">> [RECUPERACIÓN] ÉXITO: Es docente. Permitiendo el paso.");
                return ResponseEntity.ok(Map.of("mensaje", "Usuario válido, puede continuar"));
            } else {
                System.out.println(">> [RECUPERACIÓN] FALLO: Es " + usuario.getRole() + ", no docente.");
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El correo pertenece a un alumno o administrador."));
            }
        }

        System.out.println(">> [RECUPERACIÓN] FALLO: El correo no existe en la base de datos.");
        return ResponseEntity.badRequest().body(Map.of("error", "El correo no está registrado."));
    }

    // 7. GUARDAR LA NUEVA CONTRASEÑA
    @PutMapping("/restablecer-password")
    public ResponseEntity<?> restablecerPassword(@RequestBody Map<String, String> request) {
        String correo = request.get("correo");
        String nuevaPassword = request.get("nuevaPassword");

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(correo);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // ¡ENCRIPTAMOS LA NUEVA CONTRASEÑA!
            usuario.setPassword(passwordEncoder.encode(nuevaPassword));
            usuarioRepository.save(usuario);

            return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada exitosamente"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Hubo un problema al actualizar la contraseña."));
    }

    // 8. ACTUALIZAR USUARIO
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioDetalles) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setFullName(usuarioDetalles.getFullName());
            usuario.setEmail(usuarioDetalles.getEmail());
            usuario.setPrimaryPhone(usuarioDetalles.getPrimaryPhone());
            usuario.setEmergencyPhone(usuarioDetalles.getEmergencyPhone());
            usuario.setGender(usuarioDetalles.getGender());
            usuario.setBirthDate(usuarioDetalles.getBirthDate());

            // Solo actualizamos la contraseña si el usuario envió una nueva
            if (usuarioDetalles.getPassword() != null && !usuarioDetalles.getPassword().isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(usuarioDetalles.getPassword()));
            }

            usuarioRepository.save(usuario);
            return ResponseEntity.ok(Map.of("mensaje", "Usuario actualizado correctamente"));
        }
        return ResponseEntity.notFound().build();
    }

    // ==========================================
    // NUEVOS ENDPOINTS PARA RECUPERAR ALUMNO
    // ==========================================

    // A. Solicitar y enviar token
    @PostMapping("/solicitar-token")
    public ResponseEntity<?> solicitarToken(@RequestBody Map<String, String> request) {
        String correo = request.get("correo");

        System.out.println(">> [ALUMNO] Iniciando recuperación para: " + correo);

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(correo);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            System.out.println(">> [ALUMNO] Usuario encontrado. Rol en BD: '" + usuario.getRole() + "'");

            // Usamos "alumno".equalsIgnoreCase(...) para evitar problemas si getRole()
            // llegara nulo
            if ("alumno".equalsIgnoreCase(usuario.getRole())) {
                String token = String.format("%04d", new java.util.Random().nextInt(10000));
                tokensRecuperacion.put(correo, token);

                System.out.println(">> [ALUMNO] Token generado, intentando enviar correo...");

                // AQUÍ ESTÁ LA CLAVE: El Try-Catch
                try {
                    emailService.enviarTokenRecuperacion(correo, token);
                    System.out.println(">> [ALUMNO] ¡Correo enviado exitosamente!");
                    return ResponseEntity.ok(Map.of("mensaje", "Token enviado al correo"));
                } catch (Exception e) {
                    System.out.println(">> [ALUMNO - ERROR FATAL] Falló el envío del correo: " + e.getMessage());
                    e.printStackTrace(); // Esto nos dará el detalle exacto del fallo
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", "Error interno al enviar el correo."));
                }
            } else {
                System.out.println(">> [ALUMNO] Denegado: Su rol no es alumno.");
                return ResponseEntity.badRequest().body(Map.of("error", "El correo no pertenece a un alumno."));
            }
        }

        System.out.println(">> [ALUMNO] Error: El correo no existe en la base de datos.");
        return ResponseEntity.badRequest().body(Map.of("error", "El correo no está registrado o no es alumno."));
    }

    // B. Validar el token escrito por el usuario
    @PostMapping("/validar-token")
    public ResponseEntity<?> validarToken(@RequestBody Map<String, String> request) {
        String correo = request.get("correo");
        String token = request.get("token");

        // Revisamos si el token existe y coincide con el correo
        if (token != null && token.equals(tokensRecuperacion.get(correo))) {
            return ResponseEntity.ok(Map.of("mensaje", "Token válido"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Token inválido o expirado"));
    }

    // C. Cambiar la contraseña usando el token
    @PutMapping("/restablecer-password-alumno")
    public ResponseEntity<?> restablecerPasswordAlumno(@RequestBody Map<String, String> request) {
        String correo = request.get("correo");
        String token = request.get("token");
        String nuevaPassword = request.get("nuevaPassword");

        // Doble validación de seguridad
        if (token != null && token.equals(tokensRecuperacion.get(correo))) {
            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(correo);
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                usuario.setPassword(passwordEncoder.encode(nuevaPassword));
                usuarioRepository.save(usuario);

                tokensRecuperacion.remove(correo); // Borramos el token para que no se re-use
                return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada exitosamente"));
            }
        }
        return ResponseEntity.badRequest().body(Map.of("error", "El token es inválido, vuelve a solicitarlo."));
    }
}