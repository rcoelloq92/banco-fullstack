package com.banco.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * Estado de cuenta de un cliente en un rango de fechas: resumen por cuenta y
 * el detalle de movimientos. El pdfBase64 solo se incluye cuando se solicita
 * el reporte en ese formato.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReporteDto {

    private String cliente;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<CuentaReporteDto> cuentas;
    private List<MovimientoReporteDto> movimientos;
    private String pdfBase64;
}
