package com.banco.mapper;

import com.banco.dto.MovimientoDto;
import com.banco.model.Movimiento;
import org.springframework.stereotype.Component;

@Component
public class MovimientoMapper {

    public MovimientoDto aDto(Movimiento movimiento) {
        return MovimientoDto.builder()
            .id(movimiento.getId())
            .fecha(movimiento.getFecha())
            .tipoMovimiento(movimiento.getTipoMovimiento())
            .valor(movimiento.getValor())
            .saldo(movimiento.getSaldo())
            .numeroCuenta(movimiento.getCuenta().getNumeroCuenta())
            .clienteNombre(movimiento.getCuenta().getCliente().getNombre())
            .build();
    }
}
