package com.banco.service;

import com.banco.dto.ClienteDto;
import com.banco.dto.ClienteRequest;

import java.util.List;

public interface ClienteService {

    List<ClienteDto> listar();

    ClienteDto obtenerPorId(Long id);

    ClienteDto crear(ClienteRequest request);

    ClienteDto actualizar(Long id, ClienteRequest request);

    ClienteDto actualizarParcial(Long id, ClienteRequest request);

    void eliminar(Long id);
}
