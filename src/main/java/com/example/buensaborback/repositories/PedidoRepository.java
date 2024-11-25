package com.example.buensaborback.repositories;

import com.example.buensaborback.domain.entities.Cliente;
import com.example.buensaborback.domain.entities.Factura;
import com.example.buensaborback.domain.entities.Pedido;
import com.example.buensaborback.domain.entities.Sucursal;
import com.example.buensaborback.domain.entities.enums.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PedidoRepository  extends JpaRepository<Pedido, Long> {
    List<Pedido> findByFechaPedidoAndSucursal(LocalDate fecha, Sucursal sucursal);

    List<Pedido> findByAltaTrueAndCliente(Cliente cliente);

    @Query("SELECT a.denominacion, SUM(dp.cantidad) AS totalCantidad " +
            "FROM Pedido p JOIN p.detallePedidos dp JOIN dp.articulo a " +
            "WHERE p.fechaPedido BETWEEN :startDate AND :endDate " +
            "AND p.sucursal.id = :idSucursal " +
            "GROUP BY a.denominacion " +
            "ORDER BY totalCantidad DESC")
    List<Object[]> findTopProductsBySucursal(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate,
                                             @Param("idSucursal") Long idSucursal);

    @Query("select p from Pedido p where p.sucursal.id = :idSucursal and p.fechaPedido between :startDate and :endDate")
    List<Pedido> findBySucursal_IdAndFechaPedidoBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("idSucursal") Long idSucursal);



    List<Pedido> findByEstadoAndSucursal(Estado estado, Sucursal sucursal);
    Pedido findByFactura(Factura factura);

}
