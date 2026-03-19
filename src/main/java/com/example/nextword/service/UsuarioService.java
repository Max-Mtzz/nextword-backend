package com.example.nextword.service;

import com.example.nextword.model.Usuario;
import com.example.nextword.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // Inyección de dependencias
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Registra un nuevo usuario validando que el correo y el nombre de usuario no existan.
     */
    public Usuario registrarUsuario(Usuario usuario) {
        // 1. Validar unicidad del nombre de usuario
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("Error: El nombre de usuario ya está en uso.");
        }

        // 2. Validar unicidad del correo electrónico
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Error: El correo electrónico ya está registrado.");
        }

        // TODO: Aquí encriptaremos la contraseña en el futuro
        // Apartado para la encriptacion de contraseñas

        // 3. Guardar en la base de datos
        return usuarioRepository.save(usuario);
    }

    /**
     * Obtiene una lista de todos los usuarios registrados en el sistema.
     */
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    /**
     * Busca un usuario específico por su ID.
     */
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Elimina un usuario por su ID.
     */
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Error: El usuario que intentas eliminar no existe.");
        }
        usuarioRepository.deleteById(id);
    }
}