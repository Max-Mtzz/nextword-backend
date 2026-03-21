package com.example.nextword.controller;

import com.example.nextword.dto.AuthResponse;
import com.example.nextword.dto.LoginRequest;
import com.example.nextword.model.Usuario;
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
    private final PasswordEncoder passwordEncoder;     // ¡Agregamos el encriptador!

    // Actualizamos el constructor para que Spring nos inyecte las 3 cosas
    public UsuarioController(UsuarioService usuarioService, 
                             UsuarioRepository usuarioRepository, 
                             PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
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
        
        // Buscamos al usuario por correo
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(correo);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Validamos que sea docente 
            if (usuario.getRole().equalsIgnoreCase("docente")) {
                return ResponseEntity.ok(Map.of("mensaje", "Usuario válido, puede continuar"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "El correo pertenece a un alumno o administrador."));
            }
        }
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
}