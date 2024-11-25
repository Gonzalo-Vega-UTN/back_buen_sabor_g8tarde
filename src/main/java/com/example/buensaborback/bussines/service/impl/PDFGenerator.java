package com.example.buensaborback.bussines.service.impl;

import com.example.buensaborback.bussines.service.IPedidoService;
import com.example.buensaborback.domain.entities.Factura;
import com.example.buensaborback.domain.entities.DetallePedido;
import com.example.buensaborback.domain.entities.Pedido;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Stream;
@Component
public class PDFGenerator {


    private final IPedidoService pedidoService;
    @Autowired
    public PDFGenerator(@Lazy PedidoServiceImpl pedidoService) {
        this.pedidoService = pedidoService;
    }

    public  ByteArrayOutputStream generateFacturaPDF(Factura factura) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        Pedido pedido = pedidoService.findByFactura(factura);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, out);

        document.open();

        // Se agrega header
        addHeader(document);

        addInvoiceTitle(document);

        addInvoiceDetails(document, factura);

        // Detalles del cliente
        addCustomerDetails(document,pedido);

        // Tabla de Detalles del Pedido
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        addTableHeader(table);

        addRows(table, pedido);
        document.add(table);

        // footer
        addFooter(writer);

        document.close();
        return out;
    }

    private  void addHeader(Document document) throws DocumentException, IOException {

        Paragraph companyName = new Paragraph("Buen Sabor", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD));
        companyName.setAlignment(Element.ALIGN_RIGHT);

        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.addCell(createParagraphCell(companyName));

        document.add(headerTable);
        document.add(Chunk.NEWLINE);
    }

    private  void addInvoiceTitle(Document document) throws DocumentException {
        Paragraph invoiceTitle = new Paragraph("FACTURA ELECTRÓNICA", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD));
        invoiceTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(invoiceTitle);
        document.add(Chunk.NEWLINE);
    }

    private  void addInvoiceDetails(Document document, Factura factura) throws DocumentException {
        document.add(new Paragraph("Factura #" + factura.getId(), new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        document.add(new Paragraph("Fecha de Facturación: " + factura.getFechaFacturacion()));
        document.add(new Paragraph("Total Venta: $" + factura.getTotalVenta()));
        document.add(new Paragraph("Forma de Pago: " + factura.getFormaPago()));
        document.add(new Paragraph("CUIT: 25-12345678-9"));
        document.add(new Paragraph("Inicio de Actividades: 01/01/2000"));
        document.add(Chunk.NEWLINE);
    }

    private  void addCustomerDetails(Document document,Pedido pedido) throws DocumentException {
        document.add(new Paragraph("Datos del Cliente:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        document.add(new Paragraph("Nombre: "+pedido.getCliente().getNombre()));
        document.add(new Paragraph("Apellido: "+pedido.getCliente().getApellido()));
        document.add(new Paragraph("Domicilio: "+pedido.getDomicilio().getCalle()+" "+pedido.getDomicilio().getNumero()));
        document.add(Chunk.NEWLINE);
    }

    private  void addTableHeader(PdfPTable table) {
        Stream.of("Artículo", "Cantidad", "Subtotal").forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);

            table.addCell(header);
        });
    }

    private void addRows(PdfPTable table, Pedido pedido) {
        for (DetallePedido detalle : pedido.getDetallePedidos()) {
            table.addCell(detalle.getArticulo().getDenominacion());
            table.addCell(detalle.getCantidad().toString());
            table.addCell("$" + detalle.getSubTotal().toString());
        }
    }

    private  void addFooter(PdfWriter writer) {
        PdfContentByte cb = writer.getDirectContent();
        Phrase footer = new Phrase("Página 1 de 1", new Font(Font.FontFamily.HELVETICA, 8));
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                footer,
                (PageSize.A4.getLeft() + PageSize.A4.getRight()) / 2,
                PageSize.A4.getBottom() + 10, 0);
    }

    private PdfPCell createImageCell(Image image) throws DocumentException {
        PdfPCell cell = new PdfPCell(image);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private  PdfPCell createParagraphCell(Paragraph paragraph) {
        PdfPCell cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }
}