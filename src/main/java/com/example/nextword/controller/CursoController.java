package com.example.nextword.controller;

import com.example.nextword.model.Curso;
import com.example.nextword.service.CursoService;
import com.example.nextword.service.CloudinaryService; // ¡No olvides importar esto!
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // ¡Y esto para los archivos!

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoService cursoService;
    private final CloudinaryService cloudinaryService; // 1. Inyectamos Cloudinary

    public CursoController(CursoService cursoService, CloudinaryService cloudinaryService) {
        this.cursoService = cursoService;
        this.cloudinaryService = cloudinaryService;
    }

    // 1. CREAR CURSO (¡Ahora con imagen real!)
    @PostMapping
    public ResponseEntity<?> crearCurso(
            @RequestParam("imagen") MultipartFile imagen, 
            @RequestParam("nombre") String nombre) {
        
        try {
            // A. Subir la imagen a la nube y obtener el link
            String urlImagen = cloudinaryService.subirImagen(imagen);

            // B. Armar el curso
            Curso nuevoCurso = new Curso();
            nuevoCurso.setNombre(nombre);
            nuevoCurso.setUrlImagen(urlImagen); 

            // C. Guardar en Base de Datos
            Curso cursoGuardado = cursoService.crearCurso(nuevoCurso);
            
            return new ResponseEntity<>(cursoGuardado, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear el curso: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // ... (Tus otros métodos GET y DELETE se quedan exactamente igual) ...

    // 2. OBTENER TODOS
    @GetMapping
    public ResponseEntity<List<Curso>> obtenerCursos() {
        return new ResponseEntity<>(cursoService.obtenerTodos(), HttpStatus.OK);
    }

    // 3. OBTENER POR ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Curso> curso = cursoService.obtenerPorId(id);
        if (curso.isPresent()) {
            return new ResponseEntity<>(curso.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Curso no encontrado", HttpStatus.NOT_FOUND);
        }
    }

    // 4. ELIMINAR CURSO
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCurso(@PathVariable Long id) {
        cursoService.eliminarCurso(id);
        return new ResponseEntity<>("Curso eliminado exitosamente", HttpStatus.OK);
    }
}