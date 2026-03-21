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

    // --- NUEVO: ACTUALIZAR CURSO (PUT) ---
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCurso(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) { // required=false significa que la imagen es opcional
        
        try {
            Curso detallesCurso = new Curso();
            detallesCurso.setNombre(nombre);

            // Si el usuario nos manda una nueva imagen, borramos la vieja y subimos la nueva
            if (imagen != null && !imagen.isEmpty()) {
                // Buscamos el curso antiguo para borrar su imagen de la nube
                Optional<Curso> cursoAntiguo = cursoService.obtenerPorId(id);
                if (cursoAntiguo.isPresent() && cursoAntiguo.get().getUrlImagen() != null) {
                    cloudinaryService.eliminarImagen(cursoAntiguo.get().getUrlImagen());
                }
                
                // Subimos la nueva a la nube
                String nuevaUrl = cloudinaryService.subirImagen(imagen);
                detallesCurso.setUrlImagen(nuevaUrl);
            }

            // Guardamos en Base de Datos
            Curso cursoActualizado = cursoService.actualizarCurso(id, detallesCurso);
            return ResponseEntity.ok(cursoActualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar: " + e.getMessage());
        }
    }

    // --- MODIFICADO: ELIMINAR CURSO ---
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCurso(@PathVariable Long id) {
        // 1. Buscamos el curso para obtener el link de la foto
        Optional<Curso> cursoOpt = cursoService.obtenerPorId(id);
        
        if (cursoOpt.isPresent()) {
            String urlImagen = cursoOpt.get().getUrlImagen();
            
            // 2. Si tiene foto, la borramos de Cloudinary
            if (urlImagen != null && !urlImagen.isEmpty()) {
                cloudinaryService.eliminarImagen(urlImagen); 
            }
            
            // 3. Borramos el curso de la Base de Datos
            cursoService.eliminarCurso(id); 
            return new ResponseEntity<>("Curso y su imagen eliminados exitosamente", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Curso no encontrado", HttpStatus.NOT_FOUND);
        }
    }
}