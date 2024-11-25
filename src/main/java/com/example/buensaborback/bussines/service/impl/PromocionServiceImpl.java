package com.example.buensaborback.bussines.service.impl;

import com.example.buensaborback.bussines.service.*;
import com.example.buensaborback.domain.entities.*;
import com.example.buensaborback.presentation.advice.exception.BadRequestException;
import com.example.buensaborback.presentation.advice.exception.ImageUploadLimitException;
import com.example.buensaborback.presentation.advice.exception.NotFoundException;
import com.example.buensaborback.repositories.ImagenRepository;
import com.example.buensaborback.repositories.PromocionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PromocionServiceImpl implements IPromocionService {


    private final PromocionRepository promocionRepository;
    private final IPromocionDetalleService promocionDetalleService;
    private final IArtManufacturadoService artManufacturadoService;
    private final IArticuloInsumoService articuloInsumoService;
    private final ISucursalService sucursalService;
    private final ICloudinaryService cloudinaryService;
    private final ImagenRepository imagenRepository;
    private final IImagenService imagenService;
    public PromocionServiceImpl(PromocionRepository promocionRepository, PromocionDetalleServiceImpl promocionDetalleService
            , ArtManufacturadoServiceImpl artManufacturadoService, ArticuloInsumoServiceImpl articuloInsumoService,
                                ISucursalServiceImpl sucursalService, CloudinaryServiceImpl cloudinaryService, ImagenRepository imagenRepository,
                                ImagenServiceImpl imagenService) {
        this.promocionRepository = promocionRepository;
        this.promocionDetalleService = promocionDetalleService;
        this.artManufacturadoService = artManufacturadoService;
        this.articuloInsumoService = articuloInsumoService;
        this.sucursalService = sucursalService;
        this.cloudinaryService = cloudinaryService;
        this.imagenRepository = imagenRepository;
        this.imagenService = imagenService;
    }

    @Override
    public Promocion getPromocionById(Long id) {
        return promocionRepository.findById(id).orElseThrow(() -> new NotFoundException("No se encontro la promocion con ID " + id));
    }

    @Override
    public boolean existsPromocionById(Long id) {
        return promocionRepository.existsById(id);
    }

    @Override
    public Promocion create(Long idSucursal, Promocion entity) {
        Sucursal sucursal = this.sucursalService.getSucursalById(idSucursal);
        //Verificar si el nombre de la promocion ya existe, en caso de que sea asi asignarla a la sucursal
        if(promocionRepository.existsByDenominacionIgnoreCase(entity.getDenominacion())){//Si existe
            Promocion promocionExistente = this.getPromocionByDenominacion(entity.getDenominacion());
            if(!promocionExistente.isAlta()){ //si esta dada de baja significa que ninguna otra sucursal la esta utilizando
                //Pisamos los detalles de la vieja promocion por los nuevos detalles
                promocionExistente.setAlta(true);
                promocionExistente.setPrecioPromocional(entity.getPrecioPromocional());
                promocionExistente.setTipoPromocion(entity.getTipoPromocion());
                promocionExistente.setDescripcionDescuento(entity.getDescripcionDescuento());
                promocionExistente.setFechaDesde(entity.getFechaDesde());
                promocionExistente.setFechaHasta(entity.getFechaHasta());
                promocionExistente.setHoraDesde(entity.getHoraDesde());
                promocionExistente.setHoraHasta(entity.getHoraHasta());

                promocionExistente.setDetallesPromocion(entity.getDetallesPromocion());
                imagenService.updateImagenes(promocionExistente.getImagenes(), entity.getImagenes());

                sucursal.getPromociones().add(promocionExistente);
                promocionExistente.getSucursales().add(sucursal);
                return this.promocionRepository.save(promocionExistente);
            }else{//Si hay otras sucursales usando esta promocion, hay que verificar si los articulos son compatibles
                promocionExistente.getDetallesPromocion().forEach(promocionDetalle -> {
                    Articulo articulo = getArticulo(promocionDetalle);
                    if(!articulo.getSucursal().equals(sucursal)){
                        throw new BadRequestException("No puedes agregar esta promocion porque no tienes los articulos");
                    }
                });
                sucursal.getPromociones().add(promocionExistente);
                promocionExistente.getSucursales().add(sucursal);
                return this.promocionRepository.save(promocionExistente);
            }
        }else{//No existe
            sucursal.getPromociones().add(entity);
            entity.getSucursales().add(sucursal);
            for (PromocionDetalle detalle: entity.getDetallesPromocion()) {
                Articulo articulo = getArticulo(detalle);;
                detalle.setArticulo(articulo);
                detalle.setPromocion(entity);
            }
            return promocionRepository.save(entity);
        }

    }

    private Articulo getArticulo(PromocionDetalle detalle) {
        Articulo articulo;
        if(this.artManufacturadoService.existsArticuloManufacturadoById(detalle.getArticulo().getId())){
           articulo = this.artManufacturadoService.getArticuloManufacturadoById(detalle.getArticulo().getId());
        }else{
            articulo = this.articuloInsumoService.getArticuloInsumoById(detalle.getArticulo().getId());
        }
        return articulo;
    }

    @Override
    public Set<Imagen> uploadImages(MultipartFile[] files, Long idPromocion) {
        Promocion promocion = this.getPromocionById(idPromocion);
        //Se limita a un maximo de 3 imagenes por entidad
        if (promocion.getImagenes().size() > 3)
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
            promocion.getImagenes().add(image);
            //Se guarda la imagen en la base de datos
            imagenRepository.save(image);
        }

        //se actualiza el insumo en la base de datos con las imagenes
        promocionRepository.save(promocion);

        return promocion.getImagenes();
    }


    @Override
    public Promocion update(Long id, Promocion entity) {
        Promocion promocion = getPromocionById(id);
        Set<PromocionDetalle> promocionDetalles = new HashSet<>();
        for (PromocionDetalle promocionDetalle : entity.getDetallesPromocion()) {
            if(promocionDetalle.getId() != 0){
                PromocionDetalle promocionDetalleExistente = this.promocionDetalleService.getPromocionDetalleById(promocionDetalle.getId());
                promocionDetalleExistente.setCantidad(promocionDetalle.getCantidad());
                promocionDetalleExistente.setArticulo(getArticulo(promocionDetalle));
                promocionDetalles.add(promocionDetalleExistente);
            }else{
                promocionDetalle.setArticulo(getArticulo(promocionDetalle));
                promocionDetalle.setPromocion(entity);
                promocionDetalles.add(promocionDetalle);
            }

        }
        imagenService.updateImagenes(promocion.getImagenes(), entity.getImagenes());
        entity.setSucursales(entity.getSucursales().stream().map(sucursal -> sucursalService.getSucursalById(sucursal.getId())).collect(Collectors.toSet()));
        entity.setDetallesPromocion(promocionDetalles);
        return this.promocionRepository.save(entity);
    }

    @Override
    public List<Promocion> getAll() {
        return promocionRepository.findAll();
    }

    @Override
    public Promocion delete(Long idSucursal, Long idPromocion ) {
        Promocion promocion = this.getPromocionById(idPromocion);
        Sucursal sucursal = this.sucursalService.getSucursalById(idSucursal);
        promocion.getSucursales().remove(sucursal);
        sucursal.getPromociones().remove(promocion);
        if(promocion.getSucursales().isEmpty()){//Si nadie la esta utilizada pasa a estar en baja
            promocion.setAlta(false);
        }
        this.sucursalService.updateSucursal(idSucursal, sucursal);
        return this.promocionRepository.save(promocion);
    }


    @Override
    public Promocion getPromocionByDenominacion(String denominacion) {
        return promocionRepository.findByDenominacionIgnoreCase(denominacion);
    }

    @Override
    public List<Promocion> getPromocionesBySucursal(Long idSucursal) {
        Sucursal sucursal = this.sucursalService.getSucursalById(idSucursal);
        return promocionRepository.findBySucursales(sucursal);
    }
}
