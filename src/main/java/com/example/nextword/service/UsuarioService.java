package com.example.nextword.service;

import com.example.nextword.model.Usuario;
import com.example.nextword.repository.UsuarioRepository;
import com.example.nextword.security.JwtUtil;
import com.example.nextword.dto.LoginRequest;
import com.example.nextword.dto.AuthResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- Importación agregada

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; 
    private final JwtUtil jwtUtil; 

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional // <-- Agregado
    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("Error: El nombre de usuario ya está en uso.");
        }

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Error: El correo electrónico ya está registrado.");
        }

        String contrasenaEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(contrasenaEncriptada);

        return usuarioRepository.save(usuario);
    }

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Correo o contraseña incorrectos."));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new IllegalArgumentException("Correo o contraseña incorrectos.");
        }

        String token = jwtUtil.generateToken(usuario.getEmail());

        return new AuthResponse(token, usuario.getEmail(), usuario.getRole(), usuario.getId());
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional // <-- Agregado
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Error: El usuario que intentas eliminar no existe.");
        }
        usuarioRepository.deleteById(id);
    }
}