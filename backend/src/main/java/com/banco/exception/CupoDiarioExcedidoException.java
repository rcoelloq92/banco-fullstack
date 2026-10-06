package com.banco.exception;

/**
 * Se lanza cuando un débito supera el límite diario de retiro configurado.
 */
public class CupoDiarioExcedidoException extends RuntimeException {

    public CupoDiarioExcedidoException() {
        super("Cupo diario Excedido");
    }
}
