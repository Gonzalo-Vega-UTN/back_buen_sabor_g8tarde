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
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        Categoria categoriaExistente = this.getCategoriaById(id);
        List<Sucursal> sucursales = sucursalService.getSucursalesByIds(sucursalesIds);
        if (!categoriaExistente.getDenominacion().equalsIgnoreCase(body.getDenominacion())) {
            if (existsCategoriaByDenominacion(body.getDenominacion())) {
                throw new DuplicateEntryException(String.format("Ya existe una Categoria con el nombre %s", body.getDenominacion()));
            }
        }

        // Update category properties
        categoriaExistente.setAlta(true);
        categoriaExistente.setDenominacion(body.getDenominacion());
        actualizarRelacionSucursales2(categoriaExistente, sucursales);
        // Update images
        imagenService.updateImagenes(categoriaExistente.getImagenes(), body.getImagenes());
        return categoriaRepository.save(categoriaExistente);
    }

    @Override
    public Categoria create(Long idPadre, Categoria body, List<Long> sucursalesIds) {
        List<Sucursal> sucursales = sucursalService.getSucursalesByIds(sucursalesIds);

        if (idPadre != null && idPadre != 0) {
            Categoria categoriaPadre = getCategoriaById(idPadre);
            body.setCategoriaPadre(categoriaPadre);
        } else {
            body.setCategoriaPadre(null);
        }
        actualizarRelacionSucursales2(body, sucursales);
        return categoriaRepository.save(body);
    }


    private void actualizarRelacionSucursales2(Categoria categoria, List<Sucursal> nuevasSucursales) {
        // Verificar si hay variaciones entre la lista original y la nueva lista de sucursales
        if (!new HashSet<>(categoria.getSucursales()).equals(new HashSet<>(nuevasSucursales))) {
            // Obtener la lista de sucursales eliminadas
            List<Sucursal> sucursalesEliminadas = getSucursalesEliminadas(new ArrayList<>(categoria.getSucursales()), nuevasSucursales);

            for (Sucursal sucursal : sucursalesEliminadas) {
                // Eliminar la sucursal de la categoría actual
                categoria.getSucursales().removeIf(s -> s.getId().equals(sucursal.getId()));
                sucursal.getCategorias().removeIf(c -> c.getId().equals(categoria.getId()));

                // Verificar y eliminar la sucursal de las subcategorías si está presente
                for (Categoria subCategoria : categoria.getSubCategorias()) {
                    if (subCategoria.getSucursales().removeIf(s -> s.getId().equals(sucursal.getId()))) {
                        // Si se elimina de la subcategoría, eliminar la categoría de la lista de la sucursal
                        sucursal.getCategorias().removeIf(c -> c.getId().equals(subCategoria.getId()));
                    }
                }
            }

            // Agregar nuevas relaciones de sucursales
            for (Sucursal sucursal : nuevasSucursales) {
                if (categoria.getSucursales().stream().noneMatch(s -> s.getId().equals(sucursal.getId()))) {
                    categoria.getSucursales().add(sucursal);
                    sucursal.getCategorias().add(categoria);

                    // Si la categoría tiene una categoría padre, agregar la relación
                    if (categoria.getCategoriaPadre() != null) {
                        categoria.getCategoriaPadre().getSucursales().add(sucursal);
                        sucursal.getCategorias().add(categoria.getCategoriaPadre());
                    }
                }
            }
        }
    }

    public List<Sucursal> getSucursalesEliminadas(List<Sucursal> oldSucursales, List<Sucursal> newSucursales) {
        Set<Long> nuevosIds = newSucursales.stream()
                .map(Sucursal::getId)
                .collect(Collectors.toSet());

        return oldSucursales.stream()
                .filter(sucursal -> !nuevosIds.contains(sucursal.getId()))
                .collect(Collectors.toList());
    }


    private Categoria actualizarRelacionSucursales(Categoria categoria, List<Sucursal> nuevasSucursales) {
        List<Sucursal> sucursalesAntiguas = new ArrayList<>(categoria.getSucursales());

        List<Sucursal> sucursalesAEliminar = new ArrayList<>(sucursalesAntiguas);
        List<Sucursal> sucursalesAAgregar = new ArrayList<>(nuevasSucursales);

        sucursalesAEliminar.removeAll(nuevasSucursales);
        sucursalesAAgregar.removeAll(sucursalesAntiguas);

        for (Sucursal sucursalAntigua : sucursalesAEliminar) {
            try {
                Sucursal sucursalActualizada = sucursalService.getSucursalById(sucursalAntigua.getId());
                sucursalActualizada.getCategorias().remove(this.getCategoriaById(categoria.getId()));
                sucursalService.updateSucursal(sucursalActualizada.getId(), sucursalActualizada);
            } catch (Exception e) {
                System.err.println("Error al eliminar la relación con la sucursal antigua con ID: " + sucursalAntigua.getId() + " - " + e.getMessage());
            }
        }

        for (Sucursal nuevaSucursal : sucursalesAAgregar) {
            try {
                Sucursal sucursalActualizada = sucursalService.getSucursalById(nuevaSucursal.getId());
                sucursalActualizada.getCategorias().add(categoria);
                sucursalService.updateSucursal(sucursalActualizada.getId(), sucursalActualizada);

            } catch (Exception e) {
                System.err.println("Error al actualizar la sucursal con ID: " + nuevaSucursal.getId() + " - " + e.getMessage());
            }
        }

        categoria.getSucursales().clear();
        categoria.getSucursales().addAll(nuevasSucursales);

        return this.categoriaRepository.save(categoria);
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

    @Override
    public Categoria validateCategoria(String categoriaDenominacion) {
        return categoriaRepository.findByDenominacionIgnoreCase(categoriaDenominacion);
    }
}