package com.example.nextword.controller;

import com.example.nextword.dto.AuthResponse;
import com.example.nextword.dto.LoginRequest;
import com.example.nextword.model.Usuario;
import com.example.nextword.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // 1. REGISTRAR
    @PostMapping("/registro")
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody Usuario usuario) {
        // Sin try-catch. Si el correo existe, el Handler lo atrapará automáticamente.
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
        // Aquí dejamos el if-else porque Optional es una validación simple, no una Excepción.
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

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // Como tenemos el GlobalExceptionHandler, no necesitamos try-catch
        AuthResponse response = usuarioService.login(request);
        return ResponseEntity.ok(response);
    }
}