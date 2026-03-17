package com.example.nextword.controller;

import com.example.nextword.model.Usuario;
import com.example.nextword.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios") // Todas las URLs de este archivo empezarán con esto
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Inyección de dependencias
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // 1. REGISTRAR UN USUARIO (POST: http://localhost:8080/api/usuarios/registro)
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        try {
            // Intentamos guardar al usuario usando las reglas del Service
            Usuario nuevoUsuario = usuarioService.registrarUsuario(usuario);
            return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED); // 201 Created
        } catch (IllegalArgumentException e) {
            // Si el correo o usuario ya existe, cachamos el error y respondemos bonito
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 400 Bad Request
        } catch (Exception e) {
            // Si la base de datos de Oracle lo rechaza (ej. error de RegEx o contraseña corta)
            return new ResponseEntity<>("Error al guardar en la base de datos: Revisa que los datos cumplan las reglas.", HttpStatus.BAD_REQUEST);
        }
    }

    // 2. OBTENER TODOS LOS USUARIOS (GET: http://localhost:8080/api/usuarios)
    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodos() {
        return new ResponseEntity<>(usuarioService.obtenerTodosLosUsuarios(), HttpStatus.OK); // 200 OK
    }

    // 3. OBTENER UN USUARIO POR SU ID (GET: http://localhost:8080/api/usuarios/1)
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.obtenerUsuarioPorId(id);
        
        if (usuario.isPresent()) {
            return new ResponseEntity<>(usuario.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    // 4. ELIMINAR UN USUARIO (DELETE: http://localhost:8080/api/usuarios/1)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return new ResponseEntity<>("Usuario eliminado exitosamente", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}