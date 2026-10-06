package com.banco.controller;

import com.banco.dto.CuentaDto;
import com.banco.exception.RecursoNoEncontradoException;
import com.banco.model.TipoCuenta;
import com.banco.service.CuentaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CuentaService cuentaService;

    @Test
    @DisplayName("GET /api/cuentas responde 200 con la lista de cuentas")
    void listarCuentasRetorna200ConLista() throws Exception {
        CuentaDto cuenta = CuentaDto.builder()
            .id(1L)
            .numeroCuenta("478758")
            .tipoCuenta(TipoCuenta.AHORRO)
            .saldoInicial(new BigDecimal("2000.00"))
            .estado(true)
            .clienteId(1L)
            .clienteNombre("Jose Lema")
            .build();

        when(cuentaService.listar()).thenReturn(List.of(cuenta));

        mockMvc.perform(get("/api/cuentas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].numeroCuenta").value("478758"))
            .andExpect(jsonPath("$[0].clienteNombre").value("Jose Lema"));
    }

    @Test
    @DisplayName("GET /api/cuentas/{id} inexistente responde 404 con el mensaje de error")
    void obtenerCuentaInexistenteRetorna404() throws Exception {
        when(cuentaService.obtenerPorId(99L))
            .thenThrow(new RecursoNoEncontradoException("No existe una cuenta con el id 99"));

        mockMvc.perform(get("/api/cuentas/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.mensaje").value("No existe una cuenta con el id 99"));
    }
}
