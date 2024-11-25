package com.example.buensaborback.presentation.rest;

import com.example.buensaborback.bussines.service.IDomicilioService;
import com.example.buensaborback.bussines.service.ILocalidadService;
import com.example.buensaborback.bussines.service.IPaisService;
import com.example.buensaborback.bussines.service.IProvinciaService;
import com.example.buensaborback.bussines.service.impl.DomicilioServiceImpl;
import com.example.buensaborback.bussines.service.impl.LocalidadServiceImpl;
import com.example.buensaborback.bussines.service.impl.PaisServiceImpl;
import com.example.buensaborback.bussines.service.impl.ProvinciaServiceImpl;
import com.example.buensaborback.domain.entities.Domicilio;
import com.example.buensaborback.domain.entities.Localidad;
import com.example.buensaborback.domain.entities.Pais;
import com.example.buensaborback.domain.entities.Provincia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/domicilios")
@CrossOrigin("*")
public class DomicilioController {
    private final IDomicilioService domicilioService;
    private final IPaisService paisService;
    private final IProvinciaService provinciaService;
    private final ILocalidadService localidadService;

    @Autowired
    public DomicilioController(DomicilioServiceImpl DomicilioService, PaisServiceImpl paisService, ProvinciaServiceImpl provinciaService,
                               LocalidadServiceImpl localidadService) {
        this.domicilioService = DomicilioService;
        this.paisService = paisService;
        this.provinciaService = provinciaService;
        this.localidadService = localidadService;
    }

    @GetMapping("")
    public ResponseEntity<List<Domicilio>> getAll(){
        return ResponseEntity.ok().body(this.domicilioService.findAll());
    }


    @GetMapping("/localidades/{idProvincia}")
    public ResponseEntity<List<Localidad>> getLocalidadesByProvincias(@PathVariable("idProvincia") Long id){
        return ResponseEntity.ok().body(this.localidadService.findLocalidadesByProvincia(id));
    }



    @GetMapping("/provincias/{idPais}")
    public ResponseEntity<List<Provincia>> getProvinciasByPais(@PathVariable("idPais") Long id){
        return ResponseEntity.ok().body(this.provinciaService.findProvinciasByPais(id));
    }

    @GetMapping("/paises")
    public ResponseEntity<List<Pais>> getAllPaises(){
        return ResponseEntity.ok().body(this.paisService.findAllPais());
    }

    @GetMapping("/alta")
    public ResponseEntity<List<Domicilio>> getAllAlta(){
        return ResponseEntity.ok().body(this.domicilioService.findAllAlta());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Domicilio> getOne(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(this.domicilioService.getDomicilioById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Domicilio> update(@PathVariable("id") Long id, @RequestBody Domicilio body){
        return ResponseEntity.ok().body(this.domicilioService.update(id, body));
    }

    @PostMapping("")
    public ResponseEntity<Domicilio> save(@RequestBody Domicilio body){
        return ResponseEntity.ok().body(this.domicilioService.create(body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Domicilio> delete(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(this.domicilioService.delete(id));
    }
}
