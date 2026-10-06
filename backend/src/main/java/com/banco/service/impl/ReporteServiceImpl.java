package com.banco.service.impl;

import com.banco.dto.CuentaReporteDto;
import com.banco.dto.MovimientoReporteDto;
import com.banco.dto.ReporteDto;
import com.banco.exception.RecursoNoEncontradoException;
import com.banco.model.Cliente;
import com.banco.model.Cuenta;
import com.banco.model.Movimiento;
import com.banco.model.TipoMovimiento;
import com.banco.repository.ClienteRepository;
import com.banco.repository.CuentaRepository;
import com.banco.repository.MovimientoRepository;
import com.banco.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final ClienteRepository clienteRepository;
    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;
    private final GeneradorPdfReporte generadorPdfReporte;

    @Override
    @Transactional(readOnly = true)
    public ReporteDto generarEstadoCuenta(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        Cliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new RecursoNoEncontradoException(
                "No existe un cliente con el id " + clienteId));

        List<Movimiento> movimientos = movimientoRepository.findByClienteYRango(
            clienteId, fechaInicio.atStartOfDay(), fechaFin.atTime(23, 59, 59));

        Map<Long, List<Movimiento>> porCuenta = movimientos.stream()
            .collect(Collectors.groupingBy(m -> m.getCuenta().getId()));

        List<CuentaReporteDto> cuentas = cuentaRepository.findByClienteId(clienteId).stream()
            .map(cuenta -> resumenCuenta(cuenta, porCuenta.getOrDefault(cuenta.getId(), List.of())))
            .toList();

        List<MovimientoReporteDto> filas = movimientos.stream()
            .map(m -> filaReporte(cliente, m))
            .toList();

        return ReporteDto.builder()
            .cliente(cliente.getNombre())
            .fechaInicio(fechaInicio)
            .fechaFin(fechaFin)
            .cuentas(cuentas)
            .movimientos(filas)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteDto generarEstadoCuentaConPdf(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        ReporteDto reporte = generarEstadoCuenta(clienteId, fechaInicio, fechaFin);
        byte[] pdf = generadorPdfReporte.generar(reporte);
        return ReporteDto.builder()
            .cliente(reporte.getCliente())
            .fechaInicio(reporte.getFechaInicio())
            .fechaFin(reporte.getFechaFin())
            .cuentas(reporte.getCuentas())
            .movimientos(reporte.getMovimientos())
            .pdfBase64(Base64.getEncoder().encodeToString(pdf))
            .build();
    }

    /**
     * Totales del período por cuenta: los créditos se guardan en positivo y
     * los débitos en negativo, por eso se separan por signo del valor.
     */
    private CuentaReporteDto resumenCuenta(Cuenta cuenta, List<Movimiento> movimientos) {
        BigDecimal totalCreditos = sumarPorTipo(movimientos, TipoMovimiento.CREDITO);
        BigDecimal totalDebitos = sumarPorTipo(movimientos, TipoMovimiento.DEBITO);
        BigDecimal saldoDisponible = movimientos.isEmpty()
            ? cuenta.getSaldoInicial()
            : movimientos.get(movimientos.size() - 1).getSaldo();

        return CuentaReporteDto.builder()
            .numeroCuenta(cuenta.getNumeroCuenta())
            .tipoCuenta(cuenta.getTipoCuenta())
            .saldoInicial(cuenta.getSaldoInicial())
            .estado(cuenta.getEstado())
            .totalCreditos(totalCreditos)
            .totalDebitos(totalDebitos)
            .saldoDisponible(saldoDisponible)
            .build();
    }

    private BigDecimal sumarPorTipo(List<Movimiento> movimientos, TipoMovimiento tipo) {
        return movimientos.stream()
            .filter(m -> m.getTipoMovimiento() == tipo)
            .map(m -> m.getValor().abs())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * El saldo inicial de la fila es el saldo de la cuenta antes de aplicar
     * el movimiento (saldo resultante menos el valor con signo).
     */
    private MovimientoReporteDto filaReporte(Cliente cliente, Movimiento movimiento) {
        return MovimientoReporteDto.builder()
            .fecha(movimiento.getFecha().toLocalDate())
            .cliente(cliente.getNombre())
            .numeroCuenta(movimiento.getCuenta().getNumeroCuenta())
            .tipo(movimiento.getCuenta().getTipoCuenta().name())
            .saldoInicial(movimiento.getSaldo().subtract(movimiento.getValor()))
            .estado(movimiento.getCuenta().getEstado())
            .movimiento(movimiento.getValor())
            .saldoDisponible(movimiento.getSaldo())
            .build();
    }
}
