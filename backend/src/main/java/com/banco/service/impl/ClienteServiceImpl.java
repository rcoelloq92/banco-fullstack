package com.banco.service.impl;

import com.banco.dto.ClienteDto;
import com.banco.dto.ClienteRequest;
import com.banco.exception.RecursoNoEncontradoException;
import com.banco.exception.RegistroDuplicadoException;
import com.banco.mapper.ClienteMapper;
import com.banco.model.Cliente;
import com.banco.repository.ClienteRepository;
import com.banco.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDto> listar() {
        return clienteRepository.findAll().stream()
            .map(clienteMapper::aDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDto obtenerPorId(Long id) {
        return clienteMapper.aDto(buscarCliente(id));
    }

    @Override
    @Transactional
    public ClienteDto crear(ClienteRequest request) {
        if (clienteRepository.existsByClienteId(request.getClienteId())) {
            throw new RegistroDuplicadoException(
                "Ya existe un cliente con el clienteId " + request.getClienteId());
        }
        if (clienteRepository.existsByIdentificacion(request.getIdentificacion())) {
            throw new RegistroDuplicadoException(
                "Ya existe un cliente con la identificación " + request.getIdentificacion());
        }
        Cliente cliente = clienteRepository.save(clienteMapper.aEntidad(request));
        return clienteMapper.aDto(cliente);
    }

    @Override
    @Transactional
    public ClienteDto actualizar(Long id, ClienteRequest request) {
        Cliente cliente = buscarCliente(id);
        clienteMapper.aplicarCompleto(request, cliente);
        return clienteMapper.aDto(clienteRepository.save(cliente));
    }

    @Override
    @Transactional
    public ClienteDto actualizarParcial(Long id, ClienteRequest request) {
        Cliente cliente = buscarCliente(id);
        clienteMapper.aplicarParcial(request, cliente);
        return clienteMapper.aDto(clienteRepository.save(cliente));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Cliente cliente = buscarCliente(id);
        clienteRepository.delete(cliente);
    }

    private Cliente buscarCliente(Long id) {
        return clienteRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException(
                "No existe un cliente con el id " + id));
    }
}
