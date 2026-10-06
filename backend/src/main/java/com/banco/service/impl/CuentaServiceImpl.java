package com.banco.service.impl;

import com.banco.dto.CuentaDto;
import com.banco.dto.CuentaRequest;
import com.banco.exception.RecursoNoEncontradoException;
import com.banco.exception.RegistroDuplicadoException;
import com.banco.mapper.CuentaMapper;
import com.banco.model.Cliente;
import com.banco.model.Cuenta;
import com.banco.repository.ClienteRepository;
import com.banco.repository.CuentaRepository;
import com.banco.service.CuentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;
    private final CuentaMapper cuentaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CuentaDto> listar() {
        return cuentaRepository.findAll().stream()
            .map(cuentaMapper::aDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaDto obtenerPorId(Long id) {
        return cuentaMapper.aDto(buscarCuenta(id));
    }

    @Override
    @Transactional
    public CuentaDto crear(CuentaRequest request) {
        if (cuentaRepository.existsByNumeroCuenta(request.getNumeroCuenta())) {
            throw new RegistroDuplicadoException(
                "Ya existe una cuenta con el número " + request.getNumeroCuenta());
        }
        Cliente cliente = buscarCliente(request.getClienteId());
        Cuenta cuenta = cuentaRepository.save(cuentaMapper.aEntidad(request, cliente));
        return cuentaMapper.aDto(cuenta);
    }

    @Override
    @Transactional
    public CuentaDto actualizar(Long id, CuentaRequest request) {
        Cuenta cuenta = buscarCuenta(id);
        cuenta.setNumeroCuenta(request.getNumeroCuenta());
        cuenta.setTipoCuenta(request.getTipoCuenta());
        cuenta.setSaldoInicial(request.getSaldoInicial());
        cuenta.setEstado(request.getEstado());
        cuenta.setCliente(buscarCliente(request.getClienteId()));
        return cuentaMapper.aDto(cuentaRepository.save(cuenta));
    }

    @Override
    @Transactional
    public CuentaDto actualizarParcial(Long id, CuentaRequest request) {
        Cuenta cuenta = buscarCuenta(id);
        cuentaMapper.aplicarParcial(request, cuenta);
        Optional.ofNullable(request.getClienteId())
            .ifPresent(clienteId -> cuenta.setCliente(buscarCliente(clienteId)));
        return cuentaMapper.aDto(cuentaRepository.save(cuenta));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Cuenta cuenta = buscarCuenta(id);
        cuentaRepository.delete(cuenta);
    }

    private Cuenta buscarCuenta(Long id) {
        return cuentaRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException(
                "No existe una cuenta con el id " + id));
    }

    private Cliente buscarCliente(Long clienteId) {
        return clienteRepository.findById(clienteId)
            .orElseThrow(() -> new RecursoNoEncontradoException(
                "No existe un cliente con el id " + clienteId));
    }
}
