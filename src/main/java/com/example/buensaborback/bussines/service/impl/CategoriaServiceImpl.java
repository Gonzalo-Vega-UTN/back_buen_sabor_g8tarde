package com.example.buensaborback.bussines.service.impl;

import com.example.buensaborback.bussines.service.ICategoriaService;
import com.example.buensaborback.bussines.service.ICloudinaryService;
import com.example.buensaborback.bussines.service.IImagenService;
import com.example.buensaborback.domain.entities.Categoria;
import com.example.buensaborback.domain.entities.Imagen;
import com.example.buensaborback.domain.entities.Sucursal;
import com.example.buensaborback.presentation.advice.exception.BadRequestException;
import com.example.buensaborback.presentation.advice.exception.DuplicateEntryException;
import com.example.buensaborback.presentation.advice.exception.ImageUploadLimitException;
import com.example.buensaborback.presentation.advice.exception.NotFoundException;
import com.example.buensaborback.repositories.CategoriaRepository;
import com.example.buensaborback.repositories.ImagenRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
public class CategoriaServiceImpl implements ICategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final ISucursalServiceImpl sucursalService;
    private final ICloudinaryService cloudinaryService;
    private final ImagenRepository imagenRepository;
    private final IImagenService imagenService;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository, ISucursalServiceImpl sucursalService,
                                CloudinaryServiceImpl cloudinaryService, ImagenRepository imagenRepository,
                                ImagenServiceImpl imagenService) {
        this.categoriaRepository = categoriaRepository;
        this.sucursalService = sucursalService;
        this.cloudinaryService = cloudinaryService;
        this.imagenService = imagenService;
        this.imagenRepository = imagenRepository;
    }

    @Override
    public Categoria getCategoriaById(Long id) {
        return this.categoriaRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Categoria con ID %d no encontrado", id)));
    }

    @Override
    public boolean existsCategoriaById(Long id) {
        return this.categoriaRepository.existsById(id);
    }

    @Override
    public boolean existsCategoriaByDenominacion(String denominacion) {
        return categoriaRepository.existsByDenominacionIgnoreCase(denominacion);
    }

    @Override
    public List<Categoria> findAll() {
        return this.categoriaRepository.findAll();
    }

    @Override
    public List<Categoria> findAllCategoriasPadre() {
        return this.categoriaRepository.findCategoriasPadres();
    }

    @Override
    public List<Categoria> findAllAlta() {
        return this.categoriaRepository.findByAltaTrue();
    }

    @Override
    public Categoria update(Long id, Categoria body, List<Long> sucursalesIds) {
        Categoria categoria = this.getCategoriaById(id);
        List<Sucursal> sucursales = sucursalService.getSucursalesByIds(sucursalesIds);

        if(!categoria.getDenominacion().equalsIgnoreCase(body.getDenominacion())) {
            if(existsCategoriaByDenominacion(body.getDenominacion())) {
                throw new DuplicateEntryException(String.format("Ya existe una Categoria con el nombre %s", body.getDenominacion()));
            }
        }

        // Update category properties
        categoria.setDenominacion(body.getDenominacion());
        categoria.setAlta(body.isAlta());

        // Update sucursales
        categoria.getSucursales().clear();
        categoria.getSucursales().addAll(sucursales);

        // Update images
        imagenService.updateImagenes(categoria.getImagenes(), body.getImagenes());

        return this.categoriaRepository.save(categoria);
    }

    @Override
    public Categoria create(Long idPadre, Long idSucursal, Categoria body, List<Long> sucursalesIds) {
        Sucursal sucursal = sucursalService.getSucursalById(idSucursal);
        List<Sucursal> sucursales = sucursalService.getSucursalesByIds(sucursalesIds);

        if (existsCategoriaByDenominacion(body.getDenominacion())) {
            body = categoriaRepository.findByDenominacionIgnoreCase(body.getDenominacion());
        } else {
            if (idPadre != 0) {
                Categoria categoriaPadre = getCategoriaById(idPadre);
                body.setCategoriaPadre(categoriaPadre);
                categoriaPadre.getSubCategorias().add(body);
            }
        }

        actualizarCategoriaExistente(body, sucursal, sucursales);
        return categoriaRepository.save(body);
    }

    private void actualizarCategoriaExistente(Categoria body, Sucursal sucursal, List<Sucursal> sucursales) {
        body.setAlta(true);
        body.getSucursales().add(sucursal);
        body.getSucursales().addAll(sucursales);

        for (Sucursal s : sucursales) {
            s.getCategorias().add(body);
        }

        if (body.getCategoriaPadre() != null) {
            body.getCategoriaPadre().setAlta(true);
            body.getCategoriaPadre().getSucursales().add(sucursal);
            body.getCategoriaPadre().getSucursales().addAll(sucursales);
            for (Sucursal s : sucursales) {
                s.getCategorias().add(body.getCategoriaPadre());
            }
        }
    }

    @Override
    public Categoria delete(Long id, Long idSucursal) {
        Categoria categoria = this.getCategoriaById(id);
        Sucursal sucursal = this.sucursalService.getSucursalById((idSucursal));
        categoria.getSucursales().remove(sucursal);
        sucursal.getCategorias().remove(categoria);
        categoria.getSubCategorias().forEach(cat -> {
            cat.getSucursales().remove(sucursal);
            sucursal.getCategorias().remove(cat);
            this.categoriaRepository.save(cat);
        });
        if (categoria.getSucursales().isEmpty()) {
            boolean alta = false;
            categoria.setAlta(alta);
            categoria.getSubCategorias().forEach(cat -> {
                cat.setAlta(alta);
                cat.getSucursales().remove(sucursal);
                sucursal.getCategorias().remove(cat);
            });
        }
        this.sucursalService.saveSucursal(sucursal);
        return this.categoriaRepository.save(categoria);
    }

    @Override
    public List<Categoria> findAllBySucursal(Long id) {
        return this.categoriaRepository.findAllBySucursal(id);
    }

    @Override
    public List<Categoria> findAllBySucu(Long id) {
        return this.categoriaRepository.findAllBySucu(id);
    }

    @Override
    public Set<Imagen> uploadImages(MultipartFile[] files, Long idArticuloInsumo) {
        Categoria categoria = getCategoriaById(idArticuloInsumo);
        if (categoria.getImagenes().size() > 3)
            throw new ImageUploadLimitException("La maxima cantidad de imagens a subir son 3");

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new BadRequestException("El archivo esta vacio");
            }

            Imagen image = new Imagen();
            image.setName(file.getOriginalFilename());
            image.setUrl(cloudinaryService.uploadFile(file));

            if (image.getUrl() == null) {
                throw new BadRequestException("Hubo un problema al guardar la imagen");
            }

            categoria.getImagenes().add(image);
            imagenRepository.save(image);
        }

        categoriaRepository.save(categoria);

        return categoria.getImagenes();
    }
}