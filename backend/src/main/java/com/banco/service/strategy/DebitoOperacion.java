package com.banco.service.strategy;

import com.banco.exception.CupoDiarioExcedidoException;
import com.banco.exception.SaldoNoDisponibleException;
import com.banco.model.TipoMovimiento;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Débito: valida que la cuenta tenga saldo y que el retiro no supere el
 * límite diario antes de restar. El valor se persiste en negativo.
 */
@Component
public class DebitoOperacion implements OperacionMovimiento {

    @Override
    public TipoMovimiento tipo() {
        return TipoMovimiento.DEBITO;
    }

    @Override
    public ResultadoOperacion ejecutar(ContextoOperacion contexto) {
        var valor = contexto.valorSolicitado().abs();

        if (contexto.saldoActual().compareTo(BigDecimal.ZERO) <= 0
            || contexto.saldoActual().compareTo(valor) < 0) {
            throw new SaldoNoDisponibleException();
        }

        if (contexto.totalRetiradoHoy().add(valor).compareTo(contexto.limiteDiario()) > 0) {
            throw new CupoDiarioExcedidoException();
        }

        return new ResultadoOperacion(valor.negate(), contexto.saldoActual().subtract(valor));
    }
}
