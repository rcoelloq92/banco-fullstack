package com.banco.mapper;

import com.banco.dto.CuentaDto;
import com.banco.dto.CuentaRequest;
import com.banco.model.Cliente;
import com.banco.model.Cuenta;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CuentaMapper {

    public CuentaDto aDto(Cuenta cuenta) {
        return CuentaDto.builder()
            .id(cuenta.getId())
            .numeroCuenta(cuenta.getNumeroCuenta())
            .tipoCuenta(cuenta.getTipoCuenta())
            .saldoInicial(cuenta.getSaldoInicial())
            .estado(cuenta.getEstado())
            .clienteId(cuenta.getCliente().getId())
            .clienteNombre(cuenta.getCliente().getNombre())
            .build();
    }

    public Cuenta aEntidad(CuentaRequest request, Cliente cliente) {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(request.getNumeroCuenta());
        cuenta.setTipoCuenta(request.getTipoCuenta());
        cuenta.setSaldoInicial(request.getSaldoInicial());
        cuenta.setEstado(request.getEstado());
        cuenta.setCliente(cliente);
        return cuenta;
    }

    public void aplicarParcial(CuentaRequest request, Cuenta cuenta) {
        Optional.ofNullable(request.getNumeroCuenta()).ifPresent(cuenta::setNumeroCuenta);
        Optional.ofNullable(request.getTipoCuenta()).ifPresent(cuenta::setTipoCuenta);
        Optional.ofNullable(request.getSaldoInicial()).ifPresent(cuenta::setSaldoInicial);
        Optional.ofNullable(request.getEstado()).ifPresent(cuenta::setEstado);
    }
}
