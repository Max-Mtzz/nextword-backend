package com.example.nextword.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    // Spring jala automáticamente tus llaves del application.properties
    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    // Este método recibe el archivo y devuelve la URL de la nube
    public String subirImagen(MultipartFile archivo) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(archivo.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("secure_url").toString();
    }

    // Añade este método en tu CloudinaryService.java
    public void eliminarImagen(String urlImagen) {
        try {
            // Extraer el public_id de la URL (lo que va antes del .jpg o .png)
            String[] partes = urlImagen.split("/");
            String nombreArchivo = partes[partes.length - 1];
            String publicId = nombreArchivo.split("\\.")[0];

            // Le decimos a Cloudinary que la destruya
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            System.out.println("☁️ Imagen eliminada de Cloudinary: " + publicId);
        } catch (Exception e) {
            System.err.println("❌ Error al eliminar imagen de Cloudinary: " + e.getMessage());
        }
    }
}