package com.example.buensaborback.bussines.service;

import com.example.buensaborback.domain.entities.Articulo;
import com.example.buensaborback.domain.entities.Factura;
import com.example.buensaborback.domain.entities.Pedido;
import com.example.buensaborback.domain.entities.enums.Estado;

import java.time.LocalDate;
import java.util.List;

public interface IPedidoService {

    Pedido getPedidoById(Long id);
    boolean existsPedidoById(Long id);
    Pedido save(Pedido pedido);
    List<Pedido> getAll();
    List<Pedido> getAllByCliente(String user);
    List<Pedido> getAllByFecha(LocalDate fecha, Long idSucursal);
    Pedido delete(Long id);
    List<Object[]> findTopProducts(LocalDate startDate, LocalDate endDate, Long idSucursal);

    List<Pedido> findPedidosBetweenDates(LocalDate startDate, LocalDate endDate, Long idSucursal);

    List<Pedido> findByEstado(Estado estado, Long idSucursal);
    Pedido actualizarEstado(Long id, Estado estado);
    Pedido findByFactura(Factura factura);
}
