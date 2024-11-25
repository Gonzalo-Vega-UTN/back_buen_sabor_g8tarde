package com.example.buensaborback.bussines.service.impl;

import com.example.buensaborback.bussines.service.IArtManufacturadoService;
import com.example.buensaborback.bussines.service.ICloudinaryService;
import com.example.buensaborback.bussines.service.IImagenService;
import com.example.buensaborback.domain.entities.*;
import com.example.buensaborback.presentation.advice.exception.BadRequestException;
import com.example.buensaborback.presentation.advice.exception.ImageUploadLimitException;
import com.example.buensaborback.presentation.advice.exception.NotFoundException;
import com.example.buensaborback.repositories.ArticuloManufacturadoRepository;
import com.example.buensaborback.repositories.ImagenRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
public class ArtManufacturadoServiceImpl implements IArtManufacturadoService {
    private final ArticuloManufacturadoRepository articuloManufacturadoRepository;
    private final ArticuloInsumoServiceImpl articuloInsumoServiceImpl;
    private final CategoriaServiceImpl categoriaServiceImpl;
    private final UnidadMedidaServiceImpl unidadMedidaService;
    private final ArtManufacturadoDetalleServiceImpl artManufacturadoDetalleServiceImpl;
    private final PromocionDetalleServiceImpl promocionDetalleServiceImpl;
    private final ISucursalServiceImpl sucursalService;
    private final ICloudinaryService cloudinaryService;
private final ImagenRepository imagenRepository;
private final IImagenService imagenService;
    public ArtManufacturadoServiceImpl(ArticuloManufacturadoRepository articuloManufacturadoRepository,
                                       ArticuloInsumoServiceImpl articuloInsumoServiceImpl,
                                       CategoriaServiceImpl categoriaServiceImpl, UnidadMedidaServiceImpl unidadMedidaService,
                                       ArtManufacturadoDetalleServiceImpl artManufacturadoDetalleServiceImpl,
                                       PromocionDetalleServiceImpl promocionDetalleServiceImpl, ISucursalServiceImpl sucursalService,
                                       CloudinaryServiceImpl cloudinaryService, ImagenRepository imagenRepository,
                                       ImagenServiceImpl imagenService) {
        this.articuloManufacturadoRepository = articuloManufacturadoRepository;
        this.articuloInsumoServiceImpl = articuloInsumoServiceImpl;
        this.categoriaServiceImpl = categoriaServiceImpl;
        this.unidadMedidaService = unidadMedidaService;
        this.artManufacturadoDetalleServiceImpl = artManufacturadoDetalleServiceImpl;
        this.promocionDetalleServiceImpl = promocionDetalleServiceImpl;
        this.sucursalService = sucursalService;
        this.cloudinaryService = cloudinaryService;
        this.imagenRepository = imagenRepository;
        this.imagenService = imagenService;
    }

    public ArticuloManufacturado getArticuloManufacturadoById(Long id) {
        return this.articuloManufacturadoRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Articulo Manufacturado con ID %d no encontrado", id)));
    }

    public boolean existsArticuloManufacturadoById(Long id) {
        return this.articuloManufacturadoRepository.existsById(id);
    }

    @Override
    public List<ArticuloManufacturado> getAllBySucursal(Long idSucursal) {
        Sucursal sucursal = this.sucursalService.getSucursalById(idSucursal);
        return articuloManufacturadoRepository.findBySucursal(sucursal);
    }

//    @Transactional
    public ArticuloManufacturado create(ArticuloManufacturado entity,Long idSucursal) {
        entity.setSucursal(this.sucursalService.getSucursalById(idSucursal));
        entity.setCategoria(this.categoriaServiceImpl.getCategoriaById(entity.getCategoria().getId()));
        entity.setUnidadMedida(this.unidadMedidaService.getUnidadMedidaById(entity.getUnidadMedida().getId()));

        if (nonNull(entity.getPromocionDetalle())) {
            // Actualizar la lista de PromocionDetalle
            entity.setPromocionDetalle(entity.getPromocionDetalle().stream()
                    .map(detalle -> {
                        PromocionDetalle promocionDetalle = promocionDetalleServiceImpl.getPromocionDetalleById(detalle.getId());
                        promocionDetalle.setArticulo(entity);
                        return promocionDetalle;
                    })
                    .collect(Collectors.toSet()));
        }
        entity.getArticuloManufacturadoDetalles()
                .forEach(detalle -> {
                    detalle.setArticuloInsumo(this.articuloInsumoServiceImpl.getArticuloInsumoById(detalle.getArticuloInsumo().getId()));
                    detalle.setArticuloManufacturado(entity);
                });
        return this.articuloManufacturadoRepository.save(entity);
    }

    @Override
    //@Transactional
    public ArticuloManufacturado update(Long id, ArticuloManufacturado entity) {
        // Obtén el artículo manufacturado existente de la base de datos
        ArticuloManufacturado existingEntity = getArticuloManufacturadoById(id);

        // Obtener los detalles antiguos
        Set<ArticuloManufacturadoDetalle> detallesViejo = existingEntity.getArticuloManufacturadoDetalles();

        // Asignar las entidades relacionadas
        entity.setCategoria(categoriaServiceImpl.getCategoriaById(entity.getCategoria().getId())); // Si id no se encuentra, lanza una excepción en Repository
        entity.setUnidadMedida(unidadMedidaService.getUnidadMedidaById(entity.getUnidadMedida().getId())); // Si id no se encuentra, lanza una excepción en Repository

        // Prepara un nuevo conjunto para los detalles
        Set<ArticuloManufacturadoDetalle> detalles = new HashSet<>();

        for (ArticuloManufacturadoDetalle detalle : entity.getArticuloManufacturadoDetalles()) {
            // Verifica si el detalle ya existe en los detalles viejos por ArticuloInsumo ID
            ArticuloManufacturadoDetalle existingDetalle = detallesViejo.stream()
                    .filter(d -> d.getArticuloInsumo().getId().equals(detalle.getArticuloInsumo().getId()))
                    .findFirst()
                    .orElse(null);

            if (existingDetalle != null) {
                // Actualiza el detalle existente
                existingDetalle.setCantidad(detalle.getCantidad()); // Actualiza otras propiedades según sea necesario
                existingDetalle.setAlta(true); // Marca como activo
                detalles.add(existingDetalle);
            } else {
                // Si el detalle no existe, crea uno nuevo
                detalle.setArticuloManufacturado(entity); // Mantén la relación bidireccional
                detalle.setArticuloInsumo(articuloInsumoServiceImpl.getArticuloInsumoById(detalle.getArticuloInsumo().getId())); // Si id no se encuentra, lanza una excepción en Repository
                detalles.add(detalle);
            }
        }

        // Maneja los detalles que ya no están presentes en la entidad actualizada
        for (ArticuloManufacturadoDetalle detalleViejo : detallesViejo) {
            if (!detalles.contains(detalleViejo)) {
                detalleViejo.setAlta(false);
                artManufacturadoDetalleServiceImpl.create(detalleViejo);
            }
        }

        // Asigna el nuevo conjunto de detalles a la entidad principal
        entity.setArticuloManufacturadoDetalles(detalles);

        //Verificar Cambio de imagenes
        imagenService.updateImagenes(existingEntity.getImagenes(), entity.getImagenes());

        // Llama al método update de la clase base
        return this.articuloManufacturadoRepository.save(entity);
    }

    public List<ArticuloManufacturado> getAll(Optional<Long> categoriaOpt, Optional<Long> unidadMedidaOpt, Optional<String> searchOpt, Long idSucursal) {
        Sucursal sucursal = this.sucursalService.getSucursalById(idSucursal);
        Categoria categoria = categoriaOpt.map(categoriaServiceImpl::getCategoriaById).orElse(null); //Basicamente funciona así: si el Optional está vacío el map() no hace nada y salta al orElse y devuelve null, caso contrario ejecuta el metodo del map
        UnidadMedida unidadMedida = unidadMedidaOpt.map(unidadMedidaService::getUnidadMedidaById).orElse(null);
        String search = searchOpt.orElse("");

        if (categoria != null && unidadMedida != null) {
            return articuloManufacturadoRepository.findBySucursalAndCategoriaAndUnidadMedidaAndDenominacionStartingWithIgnoreCase(sucursal, categoria, unidadMedida, search);
        } else if (categoria != null) {
            return articuloManufacturadoRepository.findBySucursalAndCategoriaAndDenominacionStartingWithIgnoreCase(sucursal, categoria, search);
        } else if (unidadMedida != null) {
            return articuloManufacturadoRepository.findBySucursalAndUnidadMedidaAndDenominacionStartingWithIgnoreCase(sucursal, unidadMedida, search);
        } else if (!search.isEmpty()) {
            return articuloManufacturadoRepository.findBySucursalAndDenominacionStartingWithIgnoreCase(sucursal, search);
        } else {
            return articuloManufacturadoRepository.findBySucursal(sucursal);
        }
    }

    @Override
    public List<ArticuloManufacturado> getAll() {
        return this.articuloManufacturadoRepository.findAll();
    }

    @Override
    public ArticuloManufacturado delete(Long id) {
        ArticuloManufacturado articuloManufacturado = this.getArticuloManufacturadoById(id);
        articuloManufacturado.setAlta(!articuloManufacturado.isAlta());
        return this.articuloManufacturadoRepository.save(articuloManufacturado);
    }

    @Override
    public Set<Imagen> uploadImages(MultipartFile[] files, Long idArticuloInsumo) {
        ArticuloManufacturado insumo = getArticuloManufacturadoById(idArticuloInsumo);
        //Se limita a un maximo de 3 imagenes por entidad
        if (insumo.getImagenes().size() > 3)
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
            insumo.getImagenes().add(image);
            //Se guarda la imagen en la base de datos
            imagenRepository.save(image);
        }

        //se actualiza el insumo en la base de datos con las imagenes
        articuloManufacturadoRepository.save(insumo);

        return insumo.getImagenes();
    }

    @Override
    public List<ArticuloManufacturado> findArtManufacturadosFromCategoryAndSubcategories(Long idSucursal, Long idCategoria) {
        Sucursal sucursal = this.sucursalService.getSucursalById(idSucursal);
        if(idCategoria == 0){
            return this.articuloManufacturadoRepository.findBySucursal(sucursal);
        }
        Categoria categoria = this.categoriaServiceImpl.getCategoriaById(idCategoria);

        // Recoge los IDs de las subcategorías que pertenecen a la sucursal específica
        List<Categoria> subCategorias = new ArrayList<>(categoria.getSubCategorias().stream()
                .filter(subcategoria -> subcategoria.getSucursales().stream().anyMatch(s -> s.getId().equals(sucursal.getId())))
                .toList());

        subCategorias.add(categoria);
        // Consulta en el repositorio
        Set<ArticuloManufacturado> lista = new HashSet<>();
        for (Categoria cat : subCategorias){
            lista.addAll(articuloManufacturadoRepository.findByAltaTrueAndCategoria_IdAndSucursal_Id(cat.getId(), sucursal.getId()));
        }

        return lista.stream().toList();
    }

}
