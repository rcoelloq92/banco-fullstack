package com.banco.exception;

/**
 * Se lanza cuando se intenta un débito sin saldo suficiente en la cuenta.
 */
public class SaldoNoDisponibleException extends RuntimeException {

    public SaldoNoDisponibleException() {
        super("Saldo no disponible");
    }
}
