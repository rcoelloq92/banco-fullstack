package com.banco.service.impl;

import com.banco.dto.MovimientoReporteDto;
import com.banco.dto.ReporteDto;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Arma el PDF del estado de cuenta a partir del reporte ya calculado. Se
 * mantiene separado del servicio para que la generación del documento no se
 * mezcle con la lógica de negocio.
 */
@Component
public class GeneradorPdfReporte {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("d/M/yyyy");

    public byte[] generar(ReporteDto reporte) {
        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Document documento = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);
            PdfWriter.getInstance(documento, salida);
            documento.open();

            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            documento.add(new Paragraph("Estado de cuenta - " + reporte.getCliente(), fuenteTitulo));
            documento.add(new Paragraph(String.format("Período: %s al %s",
                reporte.getFechaInicio().format(FORMATO_FECHA),
                reporte.getFechaFin().format(FORMATO_FECHA))));
            documento.add(new Paragraph(" "));

            documento.add(tablaMovimientos(reporte.getMovimientos()));
            documento.close();
            return salida.toByteArray();
        } catch (DocumentException | java.io.IOException e) {
            throw new IllegalStateException("No fue posible generar el PDF del reporte", e);
        }
    }

    private PdfPTable tablaMovimientos(List<MovimientoReporteDto> movimientos) {
        PdfPTable tabla = new PdfPTable(8);
        tabla.setWidthPercentage(100);

        Font fuenteCabecera = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        List.of("Fecha", "Cliente", "Numero Cuenta", "Tipo", "Saldo Inicial",
                "Estado", "Movimiento", "Saldo Disponible")
            .forEach(titulo -> {
                PdfPCell celda = new PdfPCell(new Phrase(titulo, fuenteCabecera));
                celda.setBackgroundColor(new Color(33, 37, 41));
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                celda.setPadding(6);
                tabla.addCell(celda);
            });

        Font fuenteFila = FontFactory.getFont(FontFactory.HELVETICA, 9);
        movimientos.forEach(fila -> {
            tabla.addCell(new Phrase(fila.getFecha().format(FORMATO_FECHA), fuenteFila));
            tabla.addCell(new Phrase(fila.getCliente(), fuenteFila));
            tabla.addCell(new Phrase(fila.getNumeroCuenta(), fuenteFila));
            tabla.addCell(new Phrase(fila.getTipo(), fuenteFila));
            tabla.addCell(new Phrase(fila.getSaldoInicial().toPlainString(), fuenteFila));
            tabla.addCell(new Phrase(Boolean.TRUE.equals(fila.getEstado()) ? "True" : "False", fuenteFila));
            tabla.addCell(new Phrase(fila.getMovimiento().toPlainString(), fuenteFila));
            tabla.addCell(new Phrase(fila.getSaldoDisponible().toPlainString(), fuenteFila));
        });

        return tabla;
    }
}
