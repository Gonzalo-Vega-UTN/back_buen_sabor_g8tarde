package com.example.buensaborback.bussines.service;

import com.example.buensaborback.domain.entities.Imagen;
import com.example.buensaborback.domain.entities.Promocion;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface IPromocionService {
    Promocion getPromocionById(Long id);
    boolean existsPromocionById(Long id);
    Promocion create(Long idSucursal, Promocion entity);

    Set<Imagen> uploadImages(MultipartFile[] files, Long idPromocion);

    Promocion update(Long id, Promocion entity);
    List<Promocion> getAll();

    Promocion delete(Long idSucursal, Long idPromocion);

    Promocion getPromocionByDenominacion(String denominacion);

    List<Promocion> getPromocionesBySucursal(Long idSucursal);
}
