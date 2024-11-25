package com.example.buensaborback.bussines.service.impl;

import com.example.buensaborback.bussines.service.ReporteService;
import com.example.buensaborback.domain.dto.ReporteDTO;
import com.example.buensaborback.domain.entities.*;
import com.example.buensaborback.presentation.advice.exception.NotFoundException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ReporteServiceImpl implements ReporteService {

    private PedidoServiceImpl pedidoService;

    public ReporteServiceImpl(PedidoServiceImpl pedidoService) {
        this.pedidoService = pedidoService;
    }

    public List<Object[]> findTopProducts(LocalDate startDate, LocalDate endDate, Long idSucursal){
        List<Object[]> ranking = new ArrayList<>();
        ranking.addAll(pedidoService.findTopProducts(startDate,endDate, idSucursal));
        return ranking;
    }

    public List<Object[]> getTopProductsGraph(LocalDate startDate, LocalDate endDate, Long idSucursal){
        List<Object[]> ranking = new ArrayList<>();
        ranking.add(new Object[]{"Comida", "Ventas"});
        ranking.addAll(pedidoService.findTopProducts(startDate,endDate, idSucursal));
        return ranking;
    }

    public List<Object[]> getMovimientosBetweenGraph(LocalDate startDate, LocalDate endDate, Long idSucursal){
        List<Object[]> ranking = new ArrayList<>();
        ranking.add(new Object[]{"Fecha", "Ingresos", "Costos", "Ganancias"});
        ranking.addAll(this.findMovimientosBetween(startDate,endDate, idSucursal));
        return ranking;
    }

    @Override
    public List<Object[]> findMovimientosBetween(LocalDate startDate, LocalDate endDate, Long idSucursal) {
        List<Pedido> pedidos= pedidoService.findPedidosBetweenDates(startDate, endDate, idSucursal);
        List<Object[]> lista = new ArrayList<>();
        for (Pedido pedido : pedidos){
            Object[] row = new Object[4];
            row[0] = pedido.getFechaPedido().toString();
            double ingreso = 0.0;
            double costo = 0.0;
            for (DetallePedido detallePedido : pedido.getDetallePedidos()){
                Articulo articulo = detallePedido.getArticulo();
                ingreso += articulo.getPrecioVenta() * detallePedido.getCantidad();
                if(articulo instanceof ArticuloInsumo){
                    costo += ((ArticuloInsumo) articulo).getPrecioCompra() * detallePedido.getCantidad();
                }
            }
            row[1]= ingreso;
            row[2]= costo;
            row[3] = ingreso - costo;
            lista.add(row);
        }
        return lista;
    }

    public ReporteDTO findPedidosBetweenDates(LocalDate startDate, LocalDate endDate, Long idSucursal) {
        List<Pedido> pedidos= pedidoService.findPedidosBetweenDates(startDate, endDate, idSucursal);
        ReporteDTO reporteDTO=new ReporteDTO(0,0,0);
        for (Pedido p:pedidos) {
            reporteDTO.setTotalIngresos(p.getTotal()+reporteDTO.getTotalIngresos());
            reporteDTO.setTotalCostos(p.getTotalCosto()+reporteDTO.getTotalCostos());
            reporteDTO.setTotalGanancias(p.getTotal()-p.getTotalCosto()+ reporteDTO.getTotalGanancias());
        }
        return reporteDTO;
    }


    public ByteArrayInputStream generateExcelMovimientos(LocalDate startDate, LocalDate endDate, Long idSucursal) {
        try {
            return generateExcelReportMoneyMovements(startDate, endDate, idSucursal);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new NotFoundException("Hubo un Error!");
        }
    }

    public ByteArrayInputStream generateExcelRankingComidas(LocalDate startDate, LocalDate endDate, Long idSucursal) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Top_Foods");

            createHeaderRow(sheet, List.of("Comida", "Cantidad"));
            int rowNum = 1; // Empieza en la segunda fila porque la primera contiene los headers
            List<Object[]> data = this.findTopProducts(startDate, endDate, idSucursal);
            createProductRow(data, sheet, rowNum);

            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            System.out.println(e.getMessage());

            throw new NotFoundException("Hubo un Error!");
        }
    }

    private void createProductRow(List<Object[]> data, Sheet sheet, int rowIndex) {
        for (int i = 1; i < data.size(); i++) {
            Row row = sheet.createRow(rowIndex);
            row.createCell(0).setCellValue(String.valueOf(data.get(i)[0]));
            row.createCell(1).setCellValue(String.valueOf(data.get(i)[1]));
            rowIndex++;
        }
    }





    @Override
    public ByteArrayInputStream generateExcelPedidosBetween(LocalDate startDate, LocalDate endDate, Long idSucursal) throws IOException {
        List<Pedido> data = this.pedidoService.findPedidosBetweenDates(startDate, endDate, idSucursal);
        if (data.isEmpty()) {
            throw new NotFoundException("No hay datos para generar el reporte");
        }
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Sheet sheet = workbook.createSheet("Pedidos");
            createHeaderRow(sheet, List.of("Id Pedido", "Fecha Pedido", "Cliente", "Domicilio", "Forma Pago", "Estado", "Detalle", "Precio Venta", "Precio Costo", "Cantidad", "Total Costo", "Total Ingreso", "Total Ganancia"));

            int rowNum = 1; // Start from the second row for data
            for (Pedido pedido : data) {
                Row mainRow = sheet.createRow(rowNum);
                mainRow.createCell(0).setCellValue(pedido.getId());
                mainRow.createCell(1).setCellValue(pedido.getFechaPedido().toString());
                mainRow.createCell(2).setCellValue(pedido.getCliente().getNombre() + " " + pedido.getCliente().getApellido());
                String domicilioTemplate = "%s, %d, de %s, %s, Argentina.";
                mainRow.createCell(3).setCellValue(String.format(domicilioTemplate,
                        pedido.getDomicilio().getCalle(),
                        pedido.getDomicilio().getNumero(),
                        pedido.getDomicilio().getLocalidad().getNombre(),
                        pedido.getDomicilio().getLocalidad().getProvincia().getNombre()
                ));
                mainRow.createCell(4).setCellValue(pedido.getFormaPago().toString());
                mainRow.createCell(5).setCellValue(pedido.getEstado().toString());

                int contadorDetalles = rowNum;
                for (DetallePedido detalle : pedido.getDetallePedidos()) {
                    if (contadorDetalles == rowNum) {
                        // Set the main row values for the first detail
                        mainRow.createCell(6).setCellValue(detalle.getArticulo().getDenominacion());
                        mainRow.createCell(7).setCellValue(detalle.getArticulo().getPrecioVenta());
                        mainRow.createCell(8).setCellValue(detalle.getArticulo() instanceof ArticuloInsumo ? ((ArticuloInsumo) detalle.getArticulo()).getPrecioCompra() : 0);
                        mainRow.createCell(9).setCellValue(detalle.getCantidad());
                    } else {
                        Row detalleRow = sheet.createRow(contadorDetalles);
                        detalleRow.createCell(6).setCellValue(detalle.getArticulo().getDenominacion());
                        detalleRow.createCell(7).setCellValue(detalle.getArticulo().getPrecioVenta());
                        detalleRow.createCell(8).setCellValue(detalle.getArticulo() instanceof ArticuloInsumo ? ((ArticuloInsumo) detalle.getArticulo()).getPrecioCompra() : 0);
                        detalleRow.createCell(9).setCellValue(detalle.getCantidad());
                    }
                    contadorDetalles++;
                }

                mainRow.createCell(10).setCellValue(pedido.getTotalCosto());
                mainRow.createCell(11).setCellValue(pedido.getTotal());
                mainRow.createCell(12).setCellValue(pedido.getTotal() - pedido.getTotalCosto());

                // Combining cells
                CellStyle style = workbook.createCellStyle();
                style.setAlignment(HorizontalAlignment.CENTER);
                style.setVerticalAlignment(VerticalAlignment.CENTER);

                // Apply style to main row cells and ensure they are created first
                for (int col = 0; col <= 12; col++) {
                    Cell cell = sheet.getRow(rowNum).getCell(col);
                    if (cell == null) {
                        cell = sheet.getRow(rowNum).createCell(col);
                    }
                    cell.setCellStyle(style);
                }

                // Only merge if contadorDetalles > rowNum to avoid invalid ranges
                if (contadorDetalles > rowNum) {
                    sheet.addMergedRegion(new CellRangeAddress(rowNum, contadorDetalles - 1, 0, 0)); // ID
                    sheet.addMergedRegion(new CellRangeAddress(rowNum, contadorDetalles - 1, 1, 1)); // Fecha
                    sheet.addMergedRegion(new CellRangeAddress(rowNum, contadorDetalles - 1, 2, 2)); // Cliente
                    sheet.addMergedRegion(new CellRangeAddress(rowNum, contadorDetalles - 1, 3, 3)); // Domicilio
                    sheet.addMergedRegion(new CellRangeAddress(rowNum, contadorDetalles - 1, 4, 4)); // Forma de Pago
                    sheet.addMergedRegion(new CellRangeAddress(rowNum, contadorDetalles - 1, 5, 5)); // Estado
                    sheet.addMergedRegion(new CellRangeAddress(rowNum, contadorDetalles - 1, 10, 10)); // Total Costo
                    sheet.addMergedRegion(new CellRangeAddress(rowNum, contadorDetalles - 1, 11, 11)); // Total Ingreso
                    sheet.addMergedRegion(new CellRangeAddress(rowNum, contadorDetalles - 1, 12, 12)); // Total Ganancia
                }

                // Increment rowNum to the next available row
                rowNum += pedido.getDetallePedidos().size();
            }

            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } finally {
            workbook.close();
            outputStream.close();
        }
    }



    private ByteArrayInputStream generateExcelReportMoneyMovements(LocalDate startDate, LocalDate endDate, Long idSucursal) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Sheet sheet = workbook.createSheet("Movimientos");
            createHeaderRow(sheet, List.of("Fecha", "Ingresos", "Gastos", "Ganancias"));
            int rowNum = 1; // Avanzamos a la segunda fila porque la primera ya tiene el header
            List<Object[]> data = this.findMovimientosBetween(startDate, endDate, idSucursal);
            createPedidoRows(data, sheet, rowNum);

            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } finally {
            workbook.close();
            outputStream.close();
        }
    }
    private void createHeaderRow(Sheet sheet, List<String> headers) {
        Row headerRow = sheet.createRow(0);
        for (int column = 0; column < headers.size(); column++) {
            headerRow.createCell(column).setCellValue(headers.get(column));
        }
    }

    private void createPedidoRows(List<Object[]> data, Sheet sheet, int rowIndex) {
        int row2 = rowIndex;
        for (Object[] rowData : data) {
            Row row = sheet.createRow(row2);
            row.createCell(0).setCellValue(String.valueOf(rowData[0]));
            row.createCell(1).setCellValue(String.valueOf(rowData[1]));
            row.createCell(2).setCellValue(String.valueOf(rowData[2]));
            row.createCell(3).setCellValue(String.valueOf(rowData[3]));
            row2++;
        }
    }
}
