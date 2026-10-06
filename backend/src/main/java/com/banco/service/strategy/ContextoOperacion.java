package com.banco.service.strategy;

import java.math.BigDecimal;

/**
 * Datos que necesita una operación para aplicarse sobre la cuenta: el saldo
 * vigente, el valor solicitado (siempre en positivo) y la información del
 * cupo diario para validar los retiros.
 */
public record ContextoOperacion(
    BigDecimal saldoActual,
    BigDecimal valorSolicitado,
    BigDecimal totalRetiradoHoy,
    BigDecimal limiteDiario
) {
}
