package com.example.buensaborback.presentation.rest;

import com.example.buensaborback.bussines.service.IClienteService;
import com.example.buensaborback.bussines.service.impl.ClienteServiceImpl;
import com.example.buensaborback.domain.entities.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin("*")
public class ClienteController{

    private final IClienteService clienteService;

    @Autowired
    public ClienteController(ClienteServiceImpl clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok().body(this.clienteService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(this.clienteService.getClienteById(id));
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody Cliente body){
        return ResponseEntity.ok().body(this.clienteService.create(body));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody Cliente body){
        return ResponseEntity.ok().body(this.clienteService.update(id, body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(this.clienteService.delete(id));
    }

    @GetMapping("/")
    public ResponseEntity<List<Cliente>> getClientes(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido) {
        List<Cliente> clientes = clienteService.findClientes(nombre, apellido);
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> getOne(@PathVariable("username") String username){
        return ResponseEntity.ok().body(this.clienteService.getClienteByUsername(username));
    }

    @PostMapping("/uploads")
    public ResponseEntity<?> uploadImages(@RequestParam(value = "id") Long idArticulo,
                                          @RequestParam(value = "uploads") MultipartFile[] files) {
        return ResponseEntity.ok(clienteService.uploadImages(files, idArticulo));

    }

}
