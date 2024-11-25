package com.example.buensaborback.bussines.service.impl;

import com.example.buensaborback.bussines.service.ICloudinaryService;
import com.example.buensaborback.bussines.service.IEmpresaService;
import com.example.buensaborback.bussines.service.IImagenService;
import com.example.buensaborback.domain.entities.Base;
import com.example.buensaborback.domain.entities.Empresa;

import com.example.buensaborback.domain.entities.Imagen;
import com.example.buensaborback.presentation.advice.exception.*;
import com.example.buensaborback.repositories.EmpresaRepository;
import com.example.buensaborback.repositories.ImagenRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
public class IEmpresaServiceImpl implements IEmpresaService {

    private final EmpresaRepository empresaRepository;
    private final ICloudinaryService cloudinaryService;
    private final ImagenRepository imagenRepository;
    private final IImagenService imagenService;

    public IEmpresaServiceImpl(EmpresaRepository empresaRepository, CloudinaryServiceImpl cloudinaryService, ImagenRepository imagenRepository,
                               ImagenServiceImpl imagenService) {
        this.empresaRepository = empresaRepository;
        this.cloudinaryService = cloudinaryService;
        this.imagenRepository = imagenRepository;
        this.imagenService = imagenService;
    }

    @Override
    public Empresa saveEmpresa(Empresa empresa) {
        validateEmpresaNombre(empresa.getNombre());
        return empresaRepository.save(empresa);
    }

    private void validateEmpresaNombre(String nombre) {
        if(empresaRepository.existsByNombreIgnoreCase(nombre.trim())){
            throw new DuplicateEntryException(String.format("Ya existe la empresa de nombre '%s'", nombre));
        }
    }

    @Override
    public Empresa getEmpresaById(Long id){
        return this.empresaRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Empresa con ID %d no encontrado", id)));
    }
    @Override
    public boolean existsEmpresaById(Long id){
        return this.empresaRepository.existsById(id);
    }

    @Override
    public List<Empresa> getAll() {
        return empresaRepository.findAll();
    }

    @Override
    public List<Empresa> getAllAlta() {
        return empresaRepository.findByAltaTrue();
    }

    @Override
    public Empresa updateEmpresa(Long id, Empresa empresa) {
        Empresa existingEmpresa = this.getEmpresaById(id);
        if (!empresa.getNombre().equalsIgnoreCase(existingEmpresa.getNombre())) {
            validateEmpresaNombre(empresa.getNombre());
        }
        if(!empresa.isAlta() && existingEmpresa.isAlta()){

        }
        existingEmpresa.setNombre(empresa.getNombre());
        existingEmpresa.setRazonSocial(empresa.getRazonSocial());
        existingEmpresa.setCuil(empresa.getCuil());
        existingEmpresa.setSucursales(empresa.getSucursales());
        existingEmpresa.setAlta(empresa.isAlta());
        //Verificar cambio de imagenes
        imagenService.updateImagenes(existingEmpresa.getImagenes(), empresa.getImagenes());
        return empresaRepository.save(existingEmpresa);

    }

    @Override
    public Empresa changeStatus(Long id, boolean status) {
        Empresa empresa = this.getEmpresaById(id);
        if(!status && empresa.getSucursales().stream().anyMatch(Base::isAlta)){
            throw new EntityInUseException(String.format("No se puede dar de baja la empresa '%s' porque tiene sucursales activas asociadas.",
                    empresa.getNombre()));
        }
        empresa.setAlta(status);
       return empresaRepository.save(empresa);
    }

    @Override
    public Set<Imagen> uploadImages(MultipartFile[] files, Long idArticuloInsumo) {
        Empresa empresa = getEmpresaById(idArticuloInsumo);
        //Se limita a un maximo de 3 imagenes por entidad
        if (empresa.getImagenes().size() > 3)
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
            empresa.getImagenes().add(image);
            //Se guarda la imagen en la base de datos
            imagenRepository.save(image);

        }

        //se actualiza el insumo en la base de datos con las imagenes
        empresaRepository.save(empresa);

        return empresa.getImagenes();
    }
}
