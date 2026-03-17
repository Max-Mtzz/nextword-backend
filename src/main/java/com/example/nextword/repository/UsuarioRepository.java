package com.example.nextword.repository;

import com.example.nextword.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // 1. Buscar un usuario por su nombre de usuario (Útil para el Login)
    Optional<Usuario> findByUsername(String username);

    // 2. Buscar un usuario por su correo electrónico (Útil para recuperar contraseña o Login)
    Optional<Usuario> findByEmail(String email);

    // 3. Verificar si ya existe alguien con ese usuario (Útil para el Registro)
    boolean existsByUsername(String username);

    // 4. Verificar si ya existe alguien con ese correo (Útil para el Registro)
    boolean existsByEmail(String email);
}