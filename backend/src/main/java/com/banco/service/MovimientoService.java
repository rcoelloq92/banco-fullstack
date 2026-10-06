package com.banco.service;

import com.banco.dto.MovimientoDto;
import com.banco.dto.MovimientoRequest;

import java.util.List;

public interface MovimientoService {

    List<MovimientoDto> listar();

    MovimientoDto obtenerPorId(Long id);

    MovimientoDto registrar(MovimientoRequest request);

    void eliminar(Long id);
}
