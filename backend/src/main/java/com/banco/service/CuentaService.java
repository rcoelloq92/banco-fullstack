package com.banco.service;

import com.banco.dto.CuentaDto;
import com.banco.dto.CuentaRequest;

import java.util.List;

public interface CuentaService {

    List<CuentaDto> listar();

    CuentaDto obtenerPorId(Long id);

    CuentaDto crear(CuentaRequest request);

    CuentaDto actualizar(Long id, CuentaRequest request);

    CuentaDto actualizarParcial(Long id, CuentaRequest request);

    void eliminar(Long id);
}
