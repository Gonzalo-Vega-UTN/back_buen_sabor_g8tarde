package com.example.buensaborback.presentation.rest;

import com.example.buensaborback.domain.entities.Promocion;
import com.example.buensaborback.bussines.service.IPromocionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/promociones")
public class PromocionController {

    @Autowired
    private IPromocionService promocionService;

    @GetMapping
    public List<Promocion> getAllPromociones() {
        return promocionService.getAll();
    }

    @GetMapping("/sucursal/{idSucursal}")
    public List<Promocion> getPromocionesBySucursal(@PathVariable("idSucursal") Long idSucursal) {
        return promocionService.getPromocionesBySucursal(idSucursal);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Promocion> getPromocionById(@PathVariable Long id) {
        Promocion promocion = promocionService.getPromocionById(id);
        return promocion != null ? ResponseEntity.ok(promocion) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{idSucursal}")
    public Promocion createPromocion(@PathVariable("idSucursal") Long idSucursal, @RequestBody Promocion promocion) {
        return promocionService.create(idSucursal, promocion);
    }
    @PostMapping("/uploads")
    public ResponseEntity<?> uploadImages(@RequestParam(value = "id") Long idArticulo,
                                          @RequestParam(value = "uploads") MultipartFile[] files) {
        return ResponseEntity.ok(promocionService.uploadImages(files, idArticulo));

    }

    @PutMapping("/{id}")
    public ResponseEntity<Promocion> updatePromocion(@PathVariable Long id, @RequestBody Promocion promocionDetails) {
        return ResponseEntity.ok(promocionService.update(id, promocionDetails));
    }

    @DeleteMapping("/sucursal/{idSucursal}/{id}")
    public ResponseEntity<Promocion> deletePromocion(@PathVariable("idSucursal") Long idSucursal, @PathVariable Long id) {
        return ResponseEntity.ok(promocionService.delete(idSucursal, id));
    }
}
