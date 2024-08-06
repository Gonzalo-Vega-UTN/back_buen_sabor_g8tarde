package com.example.buensaborback.bussines.service;

import com.example.buensaborback.domain.entities.Categoria;
import com.example.buensaborback.domain.entities.Imagen;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface ICategoriaService {
    Categoria getCategoriaById(Long id);
    boolean existsCategoriaById(Long id);
    boolean existsCategoriaByDenominacion(String denominacion);
    List<Categoria> findAll();
    List<Categoria> findAllBySucursal(Long id);
    List<Categoria> findAllCategoriasPadre();
    List<Categoria> findAllAlta();
    Categoria update(Long id, Categoria body, List<Long> sucursalesIds);
    Categoria create(Long idPadre, Long idSucursal, Categoria body, List<Long> sucursalesIds);
    Categoria delete(Long id, Long idSucursal);
    List<Categoria> findAllBySucu(Long id);
    Set<Imagen> uploadImages(MultipartFile[] files, Long id);
}