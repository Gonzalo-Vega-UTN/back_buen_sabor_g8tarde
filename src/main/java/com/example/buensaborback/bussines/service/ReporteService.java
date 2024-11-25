package com.example.buensaborback.bussines.service;

import com.example.buensaborback.domain.dto.ReporteDTO;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public interface ReporteService {
    List<Object[]> findTopProducts(LocalDate startDate, LocalDate endDate, Long idSucursal);
    List<Object[]> getTopProductsGraph(LocalDate startDate, LocalDate endDate, Long idSucursal);
    List<Object[]> findMovimientosBetween(LocalDate startDate, LocalDate endDate, Long idSucursal);
    List<Object[]> getMovimientosBetweenGraph(LocalDate startDate, LocalDate endDate, Long idSucursal);
    ReporteDTO findPedidosBetweenDates(LocalDate startDate, LocalDate endDate, Long idSucursal);

    ByteArrayInputStream generateExcelMovimientos(LocalDate startDate, LocalDate endDate, Long idSucursal);

    ByteArrayInputStream generateExcelPedidosBetween(LocalDate startDate, LocalDate endDate, Long idSucursal) throws IOException;
    ByteArrayInputStream generateExcelRankingComidas(LocalDate startDate, LocalDate endDate, Long idSucursal);
}
