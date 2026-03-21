package com.example.nextword.service;

import com.example.nextword.model.Usuario;
import com.example.nextword.repository.UsuarioRepository;
import com.example.nextword.security.JwtUtil;
import com.example.nextword.dto.LoginRequest;
import com.example.nextword.dto.AuthResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; // Herramienta para encriptar
    private final JwtUtil jwtUtil; // Herramienta para generar el Token

    // Inyección de dependencias (Ahora inyectamos las 3 cosas)
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
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

        // 3. Encriptamos la contraseña antes de guardar
        String contrasenaEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(contrasenaEncriptada);

        // 4. Guardar en la base de datos
        return usuarioRepository.save(usuario);
    }

    /**
     * NUEVO: Verifica las credenciales y devuelve un Token JWT
     */
    public AuthResponse login(LoginRequest request) {
        // 1. Buscamos al usuario por correo
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Correo o contraseña incorrectos."));

        // 2. Comparamos la contraseña encriptada de la BD con la que mandó el usuario
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new IllegalArgumentException("Correo o contraseña incorrectos.");
        }

        // 3. Si todo está bien, generamos el Token
        String token = jwtUtil.generateToken(usuario.getEmail());

        // 4. Devolvemos el Token y los datos básicos al Frontend
        return new AuthResponse(token, usuario.getEmail(), usuario.getRole(), usuario.getId());
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