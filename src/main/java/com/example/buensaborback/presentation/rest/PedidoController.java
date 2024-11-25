package com.example.buensaborback.presentation.rest;

import com.example.buensaborback.bussines.service.IPedidoService;
import com.example.buensaborback.bussines.service.impl.PedidoServiceImpl;
import com.example.buensaborback.domain.entities.Pedido;
import com.example.buensaborback.domain.entities.enums.Estado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final IPedidoService pedidoService;

    @Autowired
    public PedidoController(PedidoServiceImpl pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping("")
    public ResponseEntity<Long> save(@RequestBody Pedido pedido) {
        try {
            Pedido nuevoPedido = pedidoService.save(pedido);
            return ResponseEntity.ok(nuevoPedido.getId());
        } catch (Exception e) {
            System.err.println("Error al guardar el pedido: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(-1L); // Retorna -1 en caso de error
        }
    }

    @GetMapping("")
    public ResponseEntity<List<Pedido>> getAll() {
        List<Pedido> pedidos = pedidoService.getAll();
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getOne(@PathVariable("id") Long id) {
        return new ResponseEntity<>(pedidoService.getPedidoById(id), HttpStatus.OK);

    }
    @GetMapping("/{idSucursal}/fecha/{fecha}")
    public ResponseEntity<List<Pedido>> getAllByFecha(@PathVariable("idSucursal") Long idSucursal, @PathVariable("fecha") LocalDate fecha) {
        List<Pedido> pedidos = pedidoService.getAllByFecha(fecha, idSucursal);
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    @GetMapping("/{idSucursal}/estado/{estado}")
    public ResponseEntity<List<Pedido>> GetByEstado(@PathVariable("idSucursal") Long idSucursal, @PathVariable("estado") Estado estado) {
        List<Pedido> pedidos = pedidoService.findByEstado(estado, idSucursal);
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }
    @PutMapping("/estado/actualizar/{id}/{estado}")
    public ResponseEntity<Pedido> updateEstado(@PathVariable("id") Long id, @PathVariable("estado") Estado estado) {
        Pedido pedidoActualizado = pedidoService.actualizarEstado(id, estado);
        return new ResponseEntity<>(pedidoActualizado, HttpStatus.OK);
    }
    @GetMapping("/cliente/{cliente}")
    public ResponseEntity<List<Pedido>> getAllByCliente(@PathVariable("cliente") String user) {
        List<Pedido> pedidos = pedidoService.getAllByCliente(user);
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }
}

