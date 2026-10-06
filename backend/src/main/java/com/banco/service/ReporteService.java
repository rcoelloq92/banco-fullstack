package com.banco.service;

import com.banco.dto.ReporteDto;

import java.time.LocalDate;

public interface ReporteService {

    ReporteDto generarEstadoCuenta(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin);

    ReporteDto generarEstadoCuentaConPdf(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin);
}
