package com.example.buensaborback.bussines.service.impl;

import com.example.buensaborback.bussines.service.IEmpleadoService;
import com.example.buensaborback.domain.entities.Empleado;
import com.example.buensaborback.domain.entities.Imagen;
import com.example.buensaborback.presentation.advice.exception.BadRequestException;
import com.example.buensaborback.presentation.advice.exception.ImageUploadLimitException;
import com.example.buensaborback.presentation.advice.exception.NotFoundException;
import com.example.buensaborback.repositories.EmpleadoRepository;
import com.example.buensaborback.repositories.ImagenRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
public class EmpleadoServiceImpl implements IEmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final CloudinaryServiceImpl cloudinaryService;
    private final ImagenRepository imagenRepository;

    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository, CloudinaryServiceImpl cloudinaryService, ImagenRepository imagenRepository) {
        this.empleadoRepository = empleadoRepository;
        this.cloudinaryService = cloudinaryService;
        this.imagenRepository = imagenRepository;
    }

    @Override
    public Empleado getEmpleadoById(Long id) {
        return this.empleadoRepository.findById(id).orElseThrow(() -> new NotFoundException("Empleado no encontrado"));
    }

    @Override
    public Empleado create(Empleado empleado) {
        return this.empleadoRepository.save(empleado);
    }

    @Override
    public Set<Imagen> uploadImages(MultipartFile[] files, Long id) {
        Empleado empleado = getEmpleadoById(id);
        //Se limita a un maximo de 3 imagenes por entidad
        if (empleado.getImagenes().size() > 3)
            throw new ImageUploadLimitException("La maxima cantidad de imagens a subir son 3");

        // Iterar sobre cada archivo recibido
        for (MultipartFile file : files) {
            // Verificar si el archivo está vacío
            if (file.isEmpty()) {
                throw new BadRequestException("El archivo esta vacio");
            }

            // Crear una entidad Image y establecer su nombre y URL (subida a Cloudinary)
            Imagen image = new Imagen();
            image.setName(file.getOriginalFilename()); // Establecer el nombre del archivo original
            image.setUrl(cloudinaryService.uploadFile(file)); // Subir el archivo a Cloudinary y obtener la URL

            // Verificar si la URL de la imagen es nula (indicativo de fallo en la subida)
            if (image.getUrl() == null) {
                throw new BadRequestException("Hubo un problema al guardar la imagen");
            }

            //Se asignan las imagenes al insumo
            empleado.getImagenes().add(image);
            //Se guarda la imagen en la base de datos
            imagenRepository.save(image);
        }

        //se actualiza el insumo en la base de datos con las imagenes
        empleadoRepository.save(empleado);

        return empleado.getImagenes();
    }
}
