package com.banco.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Centraliza el manejo de errores de la API: toda excepción se traduce a una
 * RespuestaError con el código HTTP que corresponde, de modo que los
 * controladores quedan libres de bloques try/catch.
 */
@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<RespuestaError> manejarNoEncontrado(RecursoNoEncontradoException ex) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
    }

    @ExceptionHandler(RegistroDuplicadoException.class)
    public ResponseEntity<RespuestaError> manejarDuplicado(RegistroDuplicadoException ex) {
        return construir(HttpStatus.CONFLICT, ex.getMessage(), List.of());
    }

    @ExceptionHandler({SaldoNoDisponibleException.class, CupoDiarioExcedidoException.class})
    public ResponseEntity<RespuestaError> manejarReglaNegocio(RuntimeException ex) {
        return construir(HttpStatus.BAD_REQUEST, ex.getMessage(), List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaError> manejarValidacion(MethodArgumentNotValidException ex) {
        List<String> detalles = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .toList();
        return construir(HttpStatus.BAD_REQUEST, "Datos de entrada inválidos", detalles);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RespuestaError> manejarCuerpoIlegible(HttpMessageNotReadableException ex) {
        return construir(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no tiene un formato válido", List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaError> manejarGenerico(Exception ex) {
        return construir(HttpStatus.INTERNAL_SERVER_ERROR,
            "Ocurrió un error inesperado, por favor intente nuevamente", List.of());
    }

    private ResponseEntity<RespuestaError> construir(HttpStatus estado, String mensaje, List<String> detalles) {
        return ResponseEntity.status(estado)
            .body(new RespuestaError(LocalDateTime.now(), mensaje, detalles));
    }
}
