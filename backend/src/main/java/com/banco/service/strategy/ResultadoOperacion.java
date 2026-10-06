package com.banco.service.strategy;

import java.math.BigDecimal;

/**
 * Resultado de aplicar una operación: el valor con el signo que se persiste
 * (crédito positivo, débito negativo) y el saldo resultante de la cuenta.
 */
public record ResultadoOperacion(
    BigDecimal valorConSigno,
    BigDecimal nuevoSaldo
) {
}
