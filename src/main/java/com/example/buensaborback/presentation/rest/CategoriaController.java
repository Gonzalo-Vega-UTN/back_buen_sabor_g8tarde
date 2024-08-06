package com.example.buensaborback.presentation.rest;

import com.example.buensaborback.bussines.service.ICategoriaService;
import com.example.buensaborback.domain.entities.Categoria;

import com.example.buensaborback.domain.entities.CategoriaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin("*")
public class CategoriaController {

    private final ICategoriaService categoriaService;

    @Autowired
    public CategoriaController(ICategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping("/all/{idSucursal}")
    public ResponseEntity<List<Categoria>> getAll(@PathVariable("idSucursal") Long idSucursal) {
        return ResponseEntity.ok().body(this.categoriaService.findAllBySucu(idSucursal));
    }

    @GetMapping("/padres/{idSucursal}")
    public ResponseEntity<List<Categoria>> getAllCategoriasPadres(@PathVariable("idSucursal") Long idSucursal) {
        return ResponseEntity.ok().body(this.categoriaService.findAllBySucursal(idSucursal));
    }

    @GetMapping("/alta")
    public ResponseEntity<List<Categoria>> getAllAlta() {
        return ResponseEntity.ok().body(this.categoriaService.findAllAlta());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getOne(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(this.categoriaService.getCategoriaById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> update(@PathVariable("id") Long id, @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok().body(this.categoriaService.update(id, request.getCategoria(), request.getSucursalesIds()));
    }

    @PostMapping("/{idSucursal}/{idCategoriaPadre}")
    public ResponseEntity<Categoria> save(@PathVariable("idCategoriaPadre") Long idPadre,
                                          @PathVariable("idSucursal") Long idSucursal,
                                          @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok().body(this.categoriaService.create(idPadre, idSucursal, request.getCategoria(), request.getSucursalesIds()));
    }

    @PostMapping("/uploads")
    public ResponseEntity<?> uploadImages(@RequestParam(value = "id") Long idArticulo,
                                          @RequestParam(value = "uploads") MultipartFile[] files) {
        return ResponseEntity.ok(categoriaService.uploadImages(files, idArticulo));
    }

    @DeleteMapping("/{idSucursal}/{id}")
    public ResponseEntity<Categoria> delete(@PathVariable("idSucursal") Long idSucursal, @PathVariable("id") Long id) {
        return ResponseEntity.ok().body(this.categoriaService.delete(id, idSucursal));
    }
}