package com.banco.service.strategy;

import com.banco.model.TipoMovimiento;

/**
 * Estrategia por tipo de movimiento. Cada implementación valida sus propias
 * reglas de negocio y calcula el valor con signo y el nuevo saldo, de modo
 * que agregar un nuevo tipo no obliga a modificar el servicio.
 */
public interface OperacionMovimiento {

    TipoMovimiento tipo();

    ResultadoOperacion ejecutar(ContextoOperacion contexto);
}
