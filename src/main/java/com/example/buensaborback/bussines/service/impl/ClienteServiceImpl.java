package com.example.buensaborback.bussines.service.impl;

import com.example.buensaborback.bussines.service.IClienteService;
import com.example.buensaborback.bussines.service.IDomicilioService;
import com.example.buensaborback.bussines.service.ILocalidadService;
import com.example.buensaborback.bussines.service.IUsuarioService;
import com.example.buensaborback.domain.entities.*;
import com.example.buensaborback.presentation.advice.exception.BadRequestException;
import com.example.buensaborback.presentation.advice.exception.ImageUploadLimitException;
import com.example.buensaborback.presentation.advice.exception.NotFoundException;
import com.example.buensaborback.repositories.ClienteRepository;
import com.example.buensaborback.repositories.ImagenRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClienteServiceImpl implements IClienteService {
    private final ClienteRepository clienteRepository;
    private final IUsuarioService usuarioService;
    private final IDomicilioService domicilioService;
    private  final ILocalidadService localidadService;
    private final CloudinaryServiceImpl cloudinaryService;
    private final ImagenRepository imagenRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository, UsuarioServiceImpl usuarioService, DomicilioServiceImpl domicilioService, ILocalidadService localidadService,
    CloudinaryServiceImpl cloudinaryService, ImagenRepository imagenRepository) {
        this.clienteRepository = clienteRepository;
        this.usuarioService = usuarioService;
        this.domicilioService = domicilioService;
        this.localidadService = localidadService;
        this.cloudinaryService = cloudinaryService;
        this.imagenRepository = imagenRepository;
    }
    public Cliente getClienteById(Long id){
        return this.clienteRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Cliente con ID %d no encontrado", id)));
    }

    public boolean existsClienteById(Long id){
        return this.clienteRepository.existsById(id);
    }

    @Override
    public Cliente create(Cliente entity) {

        if(entity.getUsuario().getUsername() == null){
            throw new NotFoundException("Usuario no encontrado");
        }
        entity.setUsuario(usuarioService.getUsuarioByUsername(entity.getUsuario().getUsername()));
        for (Domicilio domicilio:entity.getDomicilios()) {

            Localidad local = localidadService.getLocalidadById(domicilio.getLocalidad().getId());
            domicilio.setLocalidad(local);
        }
        entity.setAlta(true);
        return this.clienteRepository.save(entity);
    }

    @Override
    public Cliente update(Long id, Cliente entity) {
        this.getClienteById(id);
        entity.getDomicilios().forEach(System.out::println);
        Set<Domicilio> updatedDomicilios = entity.getDomicilios().stream().map(domicilio -> {
            if (domicilio.getId() != 0) {
                return domicilioService.getDomicilioById(domicilio.getId());
            }else{
                domicilio.setLocalidad(localidadService.getLocalidadById(domicilio.getLocalidad().getId()));
                domicilio.getClientes().add(entity);
                return domicilioService.create(domicilio);
            }
        }).collect(Collectors.toSet());

        entity.setDomicilios(updatedDomicilios);

        return this.clienteRepository.save(entity);
    }


    @Override
    public List<Cliente> getAll() {
        return this.clienteRepository.findAll();
    }

    @Override
    public Cliente delete(Long id) {
        Cliente cliente = this.getClienteById(id);
        cliente.setAlta(!cliente.isAlta());
        return this.clienteRepository.save(cliente);
    }

    @Override
    public List<Cliente> findClientes(String nombre, String apellido) {
        if (nombre != null && apellido != null) {
            return clienteRepository.findByNombreStartingWithIgnoreCaseAndApellidoStartingWithIgnoreCase(nombre, apellido);
        } else if (nombre != null) {
            return clienteRepository.findByNombreStartingWithIgnoreCase(nombre);
        } else if (apellido != null) {
            return clienteRepository.findByApellidoStartingWithIgnoreCase(apellido);
        } else {
            return clienteRepository.findAll();
        }
    }

    @Override
    public Cliente getClienteByUsername(String username){
        Usuario usuario = this.usuarioService.getUsuarioByUsername(username);
        return this.clienteRepository.findByUsuario(usuario);
    }

    @Override
    public Set<Imagen> uploadImages(MultipartFile[] files, Long id) {
        Cliente cliente = getClienteById(id);
        //Se limita a un maximo de 3 imagenes por entidad
        if (cliente.getImagenes().size() > 3)
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
            cliente.getImagenes().add(image);
            //Se guarda la imagen en la base de datos
            imagenRepository.save(image);
        }

        //se actualiza el insumo en la base de datos con las imagenes
        clienteRepository.save(cliente);

        return cliente.getImagenes();
    }
}
