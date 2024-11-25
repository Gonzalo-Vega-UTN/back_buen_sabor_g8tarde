package com.example.buensaborback.bussines.service;

import com.example.buensaborback.domain.entities.Imagen;

import java.util.Set;

public interface IImagenService {

    void updateImagenes(Set<Imagen> imagenesViejas, Set<Imagen> imagenesNuevas);
}
