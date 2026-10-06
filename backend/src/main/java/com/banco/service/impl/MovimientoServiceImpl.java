package com.banco.service.impl;

import com.banco.dto.MovimientoDto;
import com.banco.dto.MovimientoRequest;
import com.banco.exception.RecursoNoEncontradoException;
import com.banco.mapper.MovimientoMapper;
import com.banco.model.Cuenta;
import com.banco.model.Movimiento;
import com.banco.model.TipoMovimiento;
import com.banco.repository.CuentaRepository;
import com.banco.repository.MovimientoRepository;
import com.banco.service.MovimientoService;
import com.banco.service.strategy.ContextoOperacion;
import com.banco.service.strategy.OperacionMovimiento;
import com.banco.service.strategy.ResultadoOperacion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    private final MovimientoMapper movimientoMapper;
    private final BigDecimal limiteDiarioRetiro;

    /**
     * Las estrategias se indexan por tipo de movimiento: Spring inyecta todas
     * las implementaciones y el registro de un movimiento solo resuelve la
     * que corresponde, sin condicionales por tipo.
     */
    private final Map<TipoMovimiento, OperacionMovimiento> operaciones;

    public MovimientoServiceImpl(MovimientoRepository movimientoRepository,
                                 CuentaRepository cuentaRepository,
                                 MovimientoMapper movimientoMapper,
                                 List<OperacionMovimiento> estrategias,
                                 @Value("${aplicacion.movimientos.limite-diario-retiro}") BigDecimal limiteDiarioRetiro) {
        this.movimientoRepository = movimientoRepository;
        this.cuentaRepository = cuentaRepository;
        this.movimientoMapper = movimientoMapper;
        this.limiteDiarioRetiro = limiteDiarioRetiro;
        this.operaciones = estrategias.stream()
            .collect(Collectors.toMap(OperacionMovimiento::tipo, Function.identity(),
                (a, b) -> a, () -> new EnumMap<>(TipoMovimiento.class)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoDto> listar() {
        return movimientoRepository.findAll().stream()
            .map(movimientoMapper::aDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoDto obtenerPorId(Long id) {
        return movimientoMapper.aDto(buscarMovimiento(id));
    }

    @Override
    @Transactional
    public MovimientoDto registrar(MovimientoRequest request) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(request.getNumeroCuenta())
            .orElseThrow(() -> new RecursoNoEncontradoException(
                "No existe una cuenta con el número " + request.getNumeroCuenta()));

        OperacionMovimiento operacion = operaciones.get(request.getTipoMovimiento());

        ResultadoOperacion resultado = operacion.ejecutar(new ContextoOperacion(
            saldoDisponible(cuenta),
            request.getValor(),
            totalRetiradoHoy(cuenta),
            limiteDiarioRetiro));

        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipoMovimiento(request.getTipoMovimiento());
        movimiento.setValor(resultado.valorConSigno());
        movimiento.setSaldo(resultado.nuevoSaldo());
        movimiento.setCuenta(cuenta);

        return movimientoMapper.aDto(movimientoRepository.save(movimiento));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Movimiento movimiento = buscarMovimiento(id);
        movimientoRepository.delete(movimiento);
    }

    /**
     * El saldo disponible es el del último movimiento registrado; si la
     * cuenta no tiene movimientos se parte del saldo inicial.
     */
    private BigDecimal saldoDisponible(Cuenta cuenta) {
        return movimientoRepository.findFirstByCuentaIdOrderByFechaDescIdDesc(cuenta.getId())
            .map(Movimiento::getSaldo)
            .orElse(cuenta.getSaldoInicial());
    }

    private BigDecimal totalRetiradoHoy(Cuenta cuenta) {
        LocalDate hoy = LocalDate.now();
        return movimientoRepository.totalPorTipoYRango(
            cuenta.getId(), TipoMovimiento.DEBITO,
            hoy.atStartOfDay(), hoy.atTime(23, 59, 59));
    }

    private Movimiento buscarMovimiento(Long id) {
        return movimientoRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException(
                "No existe un movimiento con el id " + id));
    }
}
