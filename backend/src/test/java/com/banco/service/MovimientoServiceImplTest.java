package com.banco.service;

import com.banco.dto.MovimientoRequest;
import com.banco.exception.RecursoNoEncontradoException;
import com.banco.mapper.MovimientoMapper;
import com.banco.model.Cuenta;
import com.banco.model.Movimiento;
import com.banco.model.TipoMovimiento;
import com.banco.repository.CuentaRepository;
import com.banco.repository.MovimientoRepository;
import com.banco.service.impl.MovimientoServiceImpl;
import com.banco.service.strategy.CreditoOperacion;
import com.banco.service.strategy.DebitoOperacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceImplTest {

    private static final String NUMERO_CUENTA = "478758";

    @Mock
    private MovimientoRepository movimientoRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private MovimientoMapper movimientoMapper;

    private MovimientoServiceImpl service;

    private Cuenta cuenta;

    @BeforeEach
    void configurar() {
        service = new MovimientoServiceImpl(
            movimientoRepository,
            cuentaRepository,
            movimientoMapper,
            List.of(new CreditoOperacion(), new DebitoOperacion()),
            new BigDecimal("1000"));

        cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta(NUMERO_CUENTA);
        cuenta.setSaldoInicial(new BigDecimal("500.00"));
        cuenta.setEstado(true);
    }

    private MovimientoRequest request(TipoMovimiento tipo, String valor) {
        MovimientoRequest request = new MovimientoRequest();
        request.setNumeroCuenta(NUMERO_CUENTA);
        request.setTipoMovimiento(tipo);
        request.setValor(new BigDecimal(valor));
        return request;
    }

    @Test
    @DisplayName("Crédito sobre cuenta sin movimientos parte del saldo inicial")
    void registrarCreditoSobreCuentaSinMovimientosUsaSaldoInicial() {
        when(cuentaRepository.findByNumeroCuenta(NUMERO_CUENTA)).thenReturn(Optional.of(cuenta));
        when(movimientoRepository.findFirstByCuentaIdOrderByFechaDescIdDesc(1L)).thenReturn(Optional.empty());
        when(movimientoRepository.totalPorTipoYRango(anyLong(), eq(TipoMovimiento.DEBITO), any(), any()))
            .thenReturn(BigDecimal.ZERO);
        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        service.registrar(request(TipoMovimiento.CREDITO, "150.00"));

        ArgumentCaptor<Movimiento> captor = ArgumentCaptor.forClass(Movimiento.class);
        verify(movimientoRepository).save(captor.capture());
        Movimiento guardado = captor.getValue();

        assertEquals(new BigDecimal("150.00"), guardado.getValor());
        assertEquals(new BigDecimal("650.00"), guardado.getSaldo());
        assertEquals(TipoMovimiento.CREDITO, guardado.getTipoMovimiento());
        assertEquals(cuenta, guardado.getCuenta());
    }

    @Test
    @DisplayName("Débito válido descuenta del saldo del último movimiento")
    void registrarDebitoValidoDescuentaDelUltimoSaldo() {
        Movimiento ultimo = new Movimiento();
        ultimo.setSaldo(new BigDecimal("800.00"));

        when(cuentaRepository.findByNumeroCuenta(NUMERO_CUENTA)).thenReturn(Optional.of(cuenta));
        when(movimientoRepository.findFirstByCuentaIdOrderByFechaDescIdDesc(1L)).thenReturn(Optional.of(ultimo));
        when(movimientoRepository.totalPorTipoYRango(anyLong(), eq(TipoMovimiento.DEBITO), any(), any()))
            .thenReturn(new BigDecimal("100.00"));
        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        service.registrar(request(TipoMovimiento.DEBITO, "200.00"));

        ArgumentCaptor<Movimiento> captor = ArgumentCaptor.forClass(Movimiento.class);
        verify(movimientoRepository).save(captor.capture());
        Movimiento guardado = captor.getValue();

        assertEquals(new BigDecimal("-200.00"), guardado.getValor());
        assertEquals(new BigDecimal("600.00"), guardado.getSaldo());
        assertEquals(TipoMovimiento.DEBITO, guardado.getTipoMovimiento());
    }

    @Test
    @DisplayName("Movimiento sobre cuenta inexistente lanza RecursoNoEncontradoException")
    void registrarSobreCuentaInexistenteLanzaRecursoNoEncontrado() {
        when(cuentaRepository.findByNumeroCuenta(NUMERO_CUENTA)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
            () -> service.registrar(request(TipoMovimiento.CREDITO, "50.00")));

        assertEquals("No existe una cuenta con el número " + NUMERO_CUENTA, ex.getMessage());
        verify(movimientoRepository, never()).save(any());
    }
}
