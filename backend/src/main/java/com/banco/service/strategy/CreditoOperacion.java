package com.banco.service.strategy;

import com.banco.model.TipoMovimiento;
import org.springframework.stereotype.Component;

@Component
public class CreditoOperacion implements OperacionMovimiento {

    @Override
    public TipoMovimiento tipo() {
        return TipoMovimiento.CREDITO;
    }

    @Override
    public ResultadoOperacion ejecutar(ContextoOperacion contexto) {
        var valor = contexto.valorSolicitado().abs();
        return new ResultadoOperacion(valor, contexto.saldoActual().add(valor));
    }
}
