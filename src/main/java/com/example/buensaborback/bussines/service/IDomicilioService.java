package com.example.buensaborback.bussines.service;

import com.example.buensaborback.domain.entities.*;

import java.util.List;

public interface IDomicilioService {

    Domicilio getDomicilioById(Long id);
    boolean existsDomicilioById(Long id);
    Domicilio update(Long id, Domicilio body);
    Domicilio create(Domicilio body);
    Domicilio delete(Long id);
    List<Domicilio> findAll() ;
    List<Domicilio> findAllAlta();
    Localidad getLocalidadById(Long id);



}
