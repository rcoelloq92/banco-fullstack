package com.banco.dto;

import com.banco.model.TipoMovimiento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Registro de un movimiento. El valor se recibe en positivo y el signo lo
 * determina el tipo: los créditos suman y los débitos restan del saldo.
 */
@Getter
@Setter
public class MovimientoRequest {

    @NotBlank(message = "El número de cuenta es obligatorio")
    private String numeroCuenta;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private TipoMovimiento tipoMovimiento;

    @NotNull(message = "El valor del movimiento es obligatorio")
    @DecimalMin(value = "0.01", message = "El valor del movimiento debe ser mayor a cero")
    private BigDecimal valor;
}
