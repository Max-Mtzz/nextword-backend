package com.example.nextword.config;

import com.example.nextword.model.Usuario;
import com.example.nextword.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    // Inyección de dependencias
    public AdminSeeder(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Verificamos si ya existe el usuario 'admin' para no intentar crearlo dos veces
        if (!usuarioRepository.existsByUsername("admin")) {
            
            Usuario admin = new Usuario();
            
            // 2. Llenamos los datos respetando las restricciones del DFR y la BD
            admin.setUsername("admin");
            admin.setEmail("admin@utez.edu.mx"); 
            admin.setPassword("Admin1234"); // Cumple con DFR: Mínimo 8 caracteres
            admin.setRole("admin"); // Cumple con restricción CHECK ('admin')
            admin.setFullName("Administrador del Sistema"); // Cumple RegEx: Solo letras y espacios
            admin.setGender("otro"); // Cumple con restricción CHECK ('otro')
            admin.setBirthDate(LocalDate.of(1990, 1, 1)); // Fecha formato AAAA/MM/DD
            admin.setPrimaryPhone("7771234567");
            admin.setEmergencyPhone("7779876543");

            // 3. Guardamos en la base de datos de Oracle
            usuarioRepository.save(admin);
            System.out.println("Usuario Administrador por defecto creado exitosamente.");
        } else {
            System.out.println("El usuario Administrador ya se encontraba registrado en la base de datos.");
        }
    }
}