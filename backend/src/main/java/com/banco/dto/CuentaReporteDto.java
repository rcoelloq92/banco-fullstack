package com.banco.dto;

import com.banco.model.TipoCuenta;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Resumen por cuenta dentro del estado de cuenta: saldos y totales de
 * débitos y créditos del período consultado.
 */
@Getter
@Builder
public class CuentaReporteDto {

    private String numeroCuenta;
    private TipoCuenta tipoCuenta;
    private BigDecimal saldoInicial;
    private Boolean estado;
    private BigDecimal totalCreditos;
    private BigDecimal totalDebitos;
    private BigDecimal saldoDisponible;
}
