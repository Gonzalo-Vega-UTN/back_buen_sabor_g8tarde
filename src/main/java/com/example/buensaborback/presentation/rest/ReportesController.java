package com.example.buensaborback.presentation.rest;

import com.example.buensaborback.bussines.service.ReporteService;
import com.example.buensaborback.domain.dto.ReporteDTO;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes/sucursal/{idSucursal}")
@CrossOrigin("*")
public class ReportesController {
    private final ReporteService reporteService;
    @Autowired
    public ReportesController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }


    @GetMapping("/top-products")
    public List<Object[]> findTopProducts(@PathVariable("idSucursal") Long idSucursal,
                                          @RequestParam("startDate") LocalDate startDate,
                                          @RequestParam("endDate") LocalDate endDate) {
        return reporteService.getTopProductsGraph(startDate, endDate, idSucursal);


    }

    @GetMapping("/top-products/excel")
    public ResponseEntity<?> generateExcelTopProducts(@PathVariable("idSucursal") Long idSucursal,
                                                      @RequestParam("startDate") LocalDate startDate,
                                                      @RequestParam("endDate") LocalDate endDate) {
        try {
            ByteArrayInputStream excelStream = reporteService.generateExcelRankingComidas(startDate, endDate, idSucursal);
            byte[] excelBytes = IOUtils.toByteArray(excelStream);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "reporte_top_products.xlsx");
            headers.setContentLength(excelBytes.length);
            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/reporte-diario")
    public List<Object[]> findMovimientosMonetariosBetween(@PathVariable("idSucursal") Long idSucursal, @RequestParam("startDate") LocalDate startDate, @RequestParam("endDate") LocalDate endDate) {
        return reporteService.getMovimientosBetweenGraph(startDate, endDate, idSucursal);
    }
    @GetMapping("/reporte-diario/excel")
    public ResponseEntity<?> generateExcelMovimientosMonetariosBetween(@PathVariable("idSucursal") Long idSucursal, @RequestParam("startDate") LocalDate startDate, @RequestParam("endDate") LocalDate endDate) {
        try {
            ByteArrayInputStream excelStream = reporteService.generateExcelMovimientos(startDate, endDate, idSucursal);
            byte[] excelBytes = IOUtils.toByteArray(excelStream);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "reporte_movimientos.xlsx");
            headers.setContentLength(excelBytes.length);
            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pedidos/excel")
    public ResponseEntity<?> generateExcelPedidosBetween(@PathVariable("idSucursal") Long idSucursal, @RequestParam("startDate") LocalDate startDate, @RequestParam("endDate") LocalDate endDate) {
        try {
            ByteArrayInputStream excelStream = reporteService.generateExcelPedidosBetween(startDate, endDate, idSucursal);
            byte[] excelBytes = IOUtils.toByteArray(excelStream);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "reporte_pedidos.xlsx");
            headers.setContentLength(excelBytes.length);
            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/reporte-totales")
    public ReporteDTO findPedidosBetweenDates(@PathVariable("idSucursal") Long idSucursal, @RequestParam("startDate") LocalDate startDate, @RequestParam("endDate") LocalDate endDate) {
        return reporteService.findPedidosBetweenDates(startDate, endDate, idSucursal);
    }
}
