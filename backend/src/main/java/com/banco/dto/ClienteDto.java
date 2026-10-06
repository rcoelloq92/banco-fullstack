package com.banco.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Representación del cliente que expone la API. La contraseña nunca se
 * devuelve en las respuestas.
 */
@Getter
@Builder
public class ClienteDto {

    private Long id;
    private String clienteId;
    private String nombre;
    private String genero;
    private Integer edad;
    private String identificacion;
    private String direccion;
    private String telefono;
    private Boolean estado;
}
