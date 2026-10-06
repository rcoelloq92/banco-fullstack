package com.banco.service;

import com.banco.exception.CupoDiarioExcedidoException;
import com.banco.exception.SaldoNoDisponibleException;
import com.banco.service.strategy.ContextoOperacion;
import com.banco.service.strategy.CreditoOperacion;
import com.banco.service.strategy.DebitoOperacion;
import com.banco.service.strategy.ResultadoOperacion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas unitarias de las estrategias de movimiento, sin contexto de Spring.
 */
class DebitoOperacionTest {

    private static final BigDecimal LIMITE_DIARIO = new BigDecimal("1000");

    private final DebitoOperacion debito = new DebitoOperacion();
    private final CreditoOperacion credito = new CreditoOperacion();

    private ContextoOperacion contexto(String saldo, String valor, String retiradoHoy) {
        return new ContextoOperacion(
            new BigDecimal(saldo),
            new BigDecimal(valor),
            new BigDecimal(retiradoHoy),
            LIMITE_DIARIO);
    }

    @Test
    @DisplayName("Débito con saldo suficiente descuenta y persiste el valor en negativo")
    void debitoConSaldoSuficienteDescuentaYDevuelveValorNegativo() {
        ResultadoOperacion resultado = debito.ejecutar(contexto("500.00", "150.00", "0"));

        assertEquals(new BigDecimal("-150.00"), resultado.valorConSigno());
        assertEquals(new BigDecimal("350.00"), resultado.nuevoSaldo());
    }

    @Test
    @DisplayName("Débito con saldo en cero lanza SaldoNoDisponibleException")
    void debitoConSaldoEnCeroLanzaSaldoNoDisponible() {
        SaldoNoDisponibleException ex = assertThrows(SaldoNoDisponibleException.class,
            () -> debito.ejecutar(contexto("0", "10.00", "0")));

        assertEquals("Saldo no disponible", ex.getMessage());
    }

    @Test
    @DisplayName("Débito mayor al saldo disponible lanza SaldoNoDisponibleException")
    void debitoMayorAlSaldoLanzaSaldoNoDisponible() {
        assertThrows(SaldoNoDisponibleException.class,
            () -> debito.ejecutar(contexto("100.00", "100.01", "0")));
    }

    @Test
    @DisplayName("Débito que supera el cupo diario acumulado lanza CupoDiarioExcedidoException")
    void debitoQueSuperaCupoDiarioLanzaCupoExcedido() {
        // ya retiró 900 en el día; 200 adicionales superan el límite de 1000
        CupoDiarioExcedidoException ex = assertThrows(CupoDiarioExcedidoException.class,
            () -> debito.ejecutar(contexto("5000.00", "200.00", "900.00")));

        assertEquals("Cupo diario Excedido", ex.getMessage());
    }

    @Test
    @DisplayName("Crédito suma al saldo y persiste el valor en positivo")
    void creditoSumaAlSaldoYDevuelveValorPositivo() {
        ResultadoOperacion resultado = credito.ejecutar(contexto("300.00", "250.00", "0"));

        assertEquals(new BigDecimal("250.00"), resultado.valorConSigno());
        assertEquals(new BigDecimal("550.00"), resultado.nuevoSaldo());
    }
}
