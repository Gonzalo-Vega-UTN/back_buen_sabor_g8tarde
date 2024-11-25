package com.example.buensaborback.bussines.service.impl;

import com.example.buensaborback.bussines.service.IFacturaService;
import com.example.buensaborback.bussines.service.IEmailService;
import com.example.buensaborback.domain.entities.Factura;
import com.example.buensaborback.domain.entities.Pedido;
import com.example.buensaborback.repositories.FacturaRepository;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

@Service
public class FacturaServiceImpl implements IFacturaService {
    private final FacturaRepository facturaRepository;
    private final PDFGenerator pdfGenerator;
    private final IEmailService emailService;
@Autowired
    public FacturaServiceImpl(FacturaRepository facturaRepository, PDFGenerator pdfGenerator, IEmailService emailService) {
        this.facturaRepository = facturaRepository;
        this.pdfGenerator = pdfGenerator;
        this.emailService = emailService;
    }

    @Override
    public Factura crearFactura(Pedido pedido) {
        Factura factura = Factura.builder().build();
        factura.setFechaFacturacion(pedido.getFechaPedido());
        factura.setFormaPago(pedido.getFormaPago());
        factura.setTotalVenta(pedido.getTotal());
        return this.facturaRepository.save(factura);
    }

    public Optional<Factura> findById(Long facturaId) {
        return facturaRepository.findById(facturaId);
    }

   @Override
    public void enviarFacturaPorEmail(String email, Factura factura) {
        try {
            ByteArrayOutputStream pdfStream = pdfGenerator.generateFacturaPDF(factura);
            byte[] pdfBytes = pdfStream.toByteArray();

            String subject = "Su factura #" + factura.getId();
            String body = "Estimado cliente,\n\nAdjunto encontrará su factura #" + factura.getId() + ".\n\nGracias por su compra.";
            String attachmentName = "factura_" + factura.getId() + ".pdf";

            emailService.sendEmailWithAttachment(email, subject, body, pdfBytes, attachmentName);
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
    }
}