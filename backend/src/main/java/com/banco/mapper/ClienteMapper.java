package com.banco.mapper;

import com.banco.dto.ClienteDto;
import com.banco.dto.ClienteRequest;
import com.banco.model.Cliente;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClienteMapper {

    public ClienteDto aDto(Cliente cliente) {
        return ClienteDto.builder()
            .id(cliente.getId())
            .clienteId(cliente.getClienteId())
            .nombre(cliente.getNombre())
            .genero(cliente.getGenero())
            .edad(cliente.getEdad())
            .identificacion(cliente.getIdentificacion())
            .direccion(cliente.getDireccion())
            .telefono(cliente.getTelefono())
            .estado(cliente.getEstado())
            .build();
    }

    public Cliente aEntidad(ClienteRequest request) {
        Cliente cliente = new Cliente();
        aplicarCompleto(request, cliente);
        return cliente;
    }

    public void aplicarCompleto(ClienteRequest request, Cliente cliente) {
        cliente.setNombre(request.getNombre());
        cliente.setGenero(request.getGenero());
        cliente.setEdad(request.getEdad());
        cliente.setIdentificacion(request.getIdentificacion());
        cliente.setDireccion(request.getDireccion());
        cliente.setTelefono(request.getTelefono());
        cliente.setClienteId(request.getClienteId());
        cliente.setContrasena(request.getContrasena());
        cliente.setEstado(request.getEstado());
    }

    /**
     * Actualización parcial: solo se pisan los campos que llegan en la
     * petición, el resto conserva su valor actual.
     */
    public void aplicarParcial(ClienteRequest request, Cliente cliente) {
        Optional.ofNullable(request.getNombre()).ifPresent(cliente::setNombre);
        Optional.ofNullable(request.getGenero()).ifPresent(cliente::setGenero);
        Optional.ofNullable(request.getEdad()).ifPresent(cliente::setEdad);
        Optional.ofNullable(request.getIdentificacion()).ifPresent(cliente::setIdentificacion);
        Optional.ofNullable(request.getDireccion()).ifPresent(cliente::setDireccion);
        Optional.ofNullable(request.getTelefono()).ifPresent(cliente::setTelefono);
        Optional.ofNullable(request.getClienteId()).ifPresent(cliente::setClienteId);
        Optional.ofNullable(request.getContrasena()).ifPresent(cliente::setContrasena);
        Optional.ofNullable(request.getEstado()).ifPresent(cliente::setEstado);
    }
}
