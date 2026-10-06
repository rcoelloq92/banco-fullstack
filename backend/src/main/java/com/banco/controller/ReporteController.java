package com.banco.controller;

import com.banco.dto.ReporteDto;
import com.banco.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    /**
     * Estado de cuenta en JSON por cliente y rango de fechas. Con
     * formato=pdf la respuesta incluye además el documento en base64.
     */
    @GetMapping
    public ResponseEntity<ReporteDto> estadoCuenta(
        @RequestParam Long clienteId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
        @RequestParam(required = false, defaultValue = "json") String formato) {

        ReporteDto reporte = "pdf".equalsIgnoreCase(formato)
            ? reporteService.generarEstadoCuentaConPdf(clienteId, fechaInicio, fechaFin)
            : reporteService.generarEstadoCuenta(clienteId, fechaInicio, fechaFin);

        return ResponseEntity.ok(reporte);
    }
}
