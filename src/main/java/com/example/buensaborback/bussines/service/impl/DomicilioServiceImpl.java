package com.example.buensaborback.bussines.service.impl;

import com.example.buensaborback.bussines.service.IDomicilioService;
import com.example.buensaborback.bussines.service.ILocalidadService;
import com.example.buensaborback.domain.entities.Domicilio;
import com.example.buensaborback.domain.entities.Localidad;
import com.example.buensaborback.presentation.advice.exception.NotFoundException;
import com.example.buensaborback.repositories.DomicilioRepository;
import com.example.buensaborback.repositories.LocalidadRepository;
import com.example.buensaborback.repositories.PaisRepository;
import com.example.buensaborback.repositories.ProvinciaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DomicilioServiceImpl implements IDomicilioService {
    private final DomicilioRepository domicilioRepository;

    private final ILocalidadService localidadService;


    public DomicilioServiceImpl(DomicilioRepository domicilioRepository, LocalidadRepository localidadRepository, ProvinciaRepository provinciaRepository, PaisRepository paisRepository, ILocalidadService localidadService) {
        this.domicilioRepository = domicilioRepository;

        this.localidadService = localidadService;
    }




    @Override
    public Domicilio getDomicilioById(Long id){
        return this.domicilioRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Domicilio con ID %d no encontrado", id)));
    }
    @Override
    public boolean existsDomicilioById(Long id){
        return this.domicilioRepository.existsById(id);
    }

    @Override
    public List<Domicilio> findAll() {
        return this.domicilioRepository.findAll();
    }


    @Override
    public List<Domicilio> findAllAlta() {
        return this.domicilioRepository.findByAltaTrue();
    }


    @Override
    public Domicilio update(Long id, Domicilio body) {

        this.existsDomicilioById(id);

        Domicilio existingDomicilio = this.domicilioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Domicilio no encontrado con id: " + id));

        body.setLocalidad(localidadService.getLocalidadById(body.getLocalidad().getId()));
        // Actualizar los campos del domicilio
        existingDomicilio.setLocalidad(body.getLocalidad());
        existingDomicilio.setAlta(body.isAlta());
        existingDomicilio.setCp(body.getCp());
        existingDomicilio.setNumero(body.getNumero());
        existingDomicilio.setClientes(body.getClientes());
        existingDomicilio.setCalle(body.getCalle());
        return this.domicilioRepository.save(existingDomicilio);
    }
    @Override
    public Domicilio create(Domicilio body) {
        body.setLocalidad(localidadService.getLocalidadById(body.getLocalidad().getId()));
        return this.domicilioRepository.save(body);
    }
    @Override
    public Domicilio delete(Long id) {
        Domicilio domicilio = this.getDomicilioById(id);
        domicilio.setAlta(!domicilio.isAlta());
        return domicilio;
    }

    public Localidad getLocalidadById(Long id){
        return this.localidadService.getLocalidadById(id);
    }
}
