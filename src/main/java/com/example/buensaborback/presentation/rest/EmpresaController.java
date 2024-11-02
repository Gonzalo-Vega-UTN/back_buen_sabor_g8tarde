package com.example.buensaborback.presentation.rest;

import com.example.buensaborback.bussines.service.IEmpresaService;
import com.example.buensaborback.bussines.service.impl.IEmpresaServiceImpl;
import com.example.buensaborback.domain.entities.Empresa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final IEmpresaService empresaService;

    @Autowired
    public EmpresaController(IEmpresaServiceImpl empresaService) {
        this.empresaService = empresaService;
    }

    @PostMapping
    public ResponseEntity<Empresa> createEmpresa(@RequestBody Empresa empresa) {
        Empresa savedEmpresa = empresaService.saveEmpresa(empresa);
        return ResponseEntity.ok(savedEmpresa);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empresa> getEmpresaById(@PathVariable Long id) {
        Empresa empresa = empresaService.getEmpresaById(id);
        if (empresa != null) {
            return ResponseEntity.ok(empresa);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Empresa>> getAllEmpresas() {
        List<Empresa> empresas = empresaService.getAll();
        return ResponseEntity.ok(empresas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empresa> updateEmpresa(@PathVariable Long id, @RequestBody Empresa empresa) {
        Empresa updatedEmpresa = empresaService.updateEmpresa(id, empresa);
        return ResponseEntity.ok(updatedEmpresa);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Empresa> changeStatus(@PathVariable Long id, @RequestParam("status") boolean status) {
        return ResponseEntity.ok(empresaService.changeStatus(id, status));
    }

    @PostMapping("/uploads")
    public ResponseEntity<?> uploadImages(@RequestParam(value = "id") Long idArticulo,
                                          @RequestParam(value = "uploads") MultipartFile[] files) {
        return ResponseEntity.ok(empresaService.uploadImages(files, idArticulo));

    }
}
