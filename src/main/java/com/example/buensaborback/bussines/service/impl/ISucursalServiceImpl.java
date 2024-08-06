package com.example.buensaborback.bussines.service.impl;

import com.example.buensaborback.bussines.service.ICloudinaryService;
import com.example.buensaborback.bussines.service.IEmpresaService;
import com.example.buensaborback.bussines.service.IImagenService;
import com.example.buensaborback.bussines.service.ISucursalService;
import com.example.buensaborback.domain.entities.ArticuloInsumo;
import com.example.buensaborback.domain.entities.Imagen;
import com.example.buensaborback.domain.entities.Sucursal;

import com.example.buensaborback.presentation.advice.exception.BadRequestException;
import com.example.buensaborback.presentation.advice.exception.ImageUploadLimitException;
import com.example.buensaborback.presentation.advice.exception.NotFoundException;
import com.example.buensaborback.repositories.ImagenRepository;
import com.example.buensaborback.repositories.SucursalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ISucursalServiceImpl implements ISucursalService {

    private final SucursalRepository sucursalRepository;
    private final ICloudinaryService cloudinaryService;
    private final ImagenRepository imagenRepository;
    private final IImagenService imagenService;

    private final IEmpresaService empresaService;

    public ISucursalServiceImpl(SucursalRepository sucursalRepository, CloudinaryServiceImpl cloudinaryService,
                                ImagenRepository imagenRepository, ImagenServiceImpl imagenService,
                                IEmpresaServiceImpl empresaService) {
        this.sucursalRepository = sucursalRepository;
        this.cloudinaryService = cloudinaryService;
        this.imagenRepository = imagenRepository;
        this.imagenService = imagenService;
        this.empresaService = empresaService;
    }

    @Override
    public Sucursal saveSucursal(Sucursal sucursal) {
        System.out.println(sucursal);
        sucursal.setEmpresa(empresaService.getEmpresaById(sucursal.getEmpresa().getId()));

        return sucursalRepository.save(sucursal);
    }

    @Override
    public Sucursal getSucursalById(Long id) {
        return sucursalRepository.findById(id).orElseThrow(() -> new NotFoundException("La Sucursal con id " + id + " no se encuntra"));
    }

    @Override
    public List<Sucursal> getAllSucursales() {
        return sucursalRepository.findAll();
    }

    @Override
    public List<Sucursal> getSucursalesByIds(List<Long> ids) {
        return sucursalRepository.findAllById(ids);
    }

    @Override
    public Sucursal updateSucursal(Long id, Sucursal sucursal) {
       Sucursal existingSucursal = getSucursalById(id);
            Sucursal updatedSucursal = existingSucursal;
            updatedSucursal.setNombre(sucursal.getNombre());
            updatedSucursal.setHorarioApertura(sucursal.getHorarioApertura());
            updatedSucursal.setHorarioCierre(sucursal.getHorarioCierre());

        //Verificar
        imagenService.updateImagenes(existingSucursal.getImagenes(), updatedSucursal.getImagenes());
            return sucursalRepository.save(updatedSucursal);
    }

    @Override
    public void deleteSucursal(Long id) {
        sucursalRepository.deleteById(id);
    }

    @Override
    public List<Sucursal> getSucursalesByEmpresaId(Long empresaId) {
        return sucursalRepository.findByEmpresaId(empresaId);
    }

    @Override
    public Set<Imagen> uploadImages(MultipartFile[] files, Long idArticuloInsumo) {
        Sucursal sucursal = getSucursalById(idArticuloInsumo);
        //Se limita a un maximo de 3 imagenes por entidad
        if (sucursal.getImagenes().size() > 3)
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
            sucursal.getImagenes().add(image);
            //Se guarda la imagen en la base de datos
            imagenRepository.save(image);
        }

        //se actualiza el insumo en la base de datos con las imagenes
        sucursalRepository.save(sucursal);

        return sucursal.getImagenes();
    }
}
