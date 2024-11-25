package com.example.buensaborback.bussines.service.impl;

import com.example.buensaborback.bussines.service.ILocalidadService;
import com.example.buensaborback.bussines.service.IProvinciaService;
import com.example.buensaborback.domain.entities.Localidad;
import com.example.buensaborback.domain.entities.Pais;
import com.example.buensaborback.domain.entities.Provincia;
import com.example.buensaborback.presentation.advice.exception.NotFoundException;
import com.example.buensaborback.repositories.LocalidadRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocalidadServiceImpl implements ILocalidadService {

    private LocalidadRepository localidadRepository;
    private IProvinciaService provinciaService;

    public LocalidadServiceImpl(LocalidadRepository localidadRepository, ProvinciaServiceImpl provinciaService){
        this.localidadRepository = localidadRepository;
        this.provinciaService = provinciaService;
    }

    @Override
    public List<Localidad> findLocalidadesByProvincia(Long idProvincia) {
        if(idProvincia == 0) return this.localidadRepository.findAll();
        Provincia provincia =  this.provinciaService.getProvinciaById(idProvincia);
        return this.localidadRepository.findByProvincia(provincia);
    }

    @Override
    public Localidad getLocalidadById(Long idLocalidad){
        return this.localidadRepository.findById(idLocalidad).orElseThrow(() -> new NotFoundException(String.format("Localidad con ID %d no encontrado", idLocalidad)));
    }
    public boolean existLocalidadById(Long idLocalidad){
        return this.localidadRepository.existsById(idLocalidad);
    }

    public List<Localidad> getAll(){
        return this.localidadRepository.findAll();
    }
}
