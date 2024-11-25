package com.example.buensaborback.bussines.service.impl;

import com.example.buensaborback.bussines.service.ICloudinaryService;
import com.example.buensaborback.bussines.service.IImagenService;
import com.example.buensaborback.domain.entities.Imagen;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ImagenServiceImpl implements IImagenService {

    private final ICloudinaryService cloudinaryService;

    public ImagenServiceImpl( CloudinaryServiceImpl cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public void updateImagenes(Set<Imagen> imagenesViejas, Set<Imagen> imagenesNuevas) {
        // Encontrar las imágenes que ya no están en el nuevo conjunto
        Set<Imagen> imagesToRemove = imagenesViejas.stream()
                .filter(oldImage -> !imagenesNuevas.contains(oldImage))
                .collect(Collectors.toSet());

        // Eliminar las imágenes obsoletas de Cloudinary
        for (Imagen image : imagesToRemove) {
            String publicId = image.getUrl().split("/")[image.getUrl().split("/").length -1]; //El publicId es el final de la url

            boolean result = cloudinaryService.deleteImage(publicId);
            log.warn(String.format("Eliminando imagen %s. Operation Status: %s", publicId, result));
        }
        imagenesViejas.clear();
        imagenesViejas.addAll(imagenesNuevas);
    }
}
