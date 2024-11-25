package com.example.buensaborback.bussines.service;

import com.example.buensaborback.domain.entities.Empleado;
import com.example.buensaborback.domain.entities.Imagen;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface IEmpleadoService {

    Empleado getEmpleadoById(Long id);
    Empleado create(Empleado empleado);
    Set<Imagen> uploadImages(MultipartFile[] files, Long id);
}
