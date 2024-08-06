package com.example.buensaborback.bussines.service;

import com.example.buensaborback.domain.entities.Imagen;
import com.example.buensaborback.domain.entities.Sucursal;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface ISucursalService {
    Sucursal saveSucursal(Sucursal sucursal);
    Sucursal getSucursalById(Long id);
    List<Sucursal> getAllSucursales();
    Sucursal updateSucursal(Long id, Sucursal sucursal);
    void deleteSucursal(Long id);
    List<Sucursal> getSucursalesByEmpresaId(Long empresaId);

    List<Sucursal> getSucursalesByIds(List<Long> ids);
    Set<Imagen> uploadImages(MultipartFile[] files, Long id);
}
