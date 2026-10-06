package com.banco.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Estructura única de error que consume el frontend para mostrar los
 * mensajes de validación en pantalla.
 */
@Getter
@AllArgsConstructor
public class RespuestaError {

    private LocalDateTime fecha;
    private String mensaje;
    private List<String> detalles;
}
