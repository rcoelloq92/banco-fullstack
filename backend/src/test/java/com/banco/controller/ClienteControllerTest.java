package com.banco.controller;

import com.banco.dto.ClienteDto;
import com.banco.dto.ClienteRequest;
import com.banco.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClienteService clienteService;

    private ClienteDto clienteDto() {
        return ClienteDto.builder()
            .id(1L)
            .clienteId("jose01")
            .nombre("Jose Lema")
            .genero("M")
            .edad(35)
            .identificacion("0912345678")
            .direccion("Otavalo sn y principal")
            .telefono("098254785")
            .estado(true)
            .build();
    }

    private ClienteRequest clienteRequest() {
        ClienteRequest request = new ClienteRequest();
        request.setNombre("Jose Lema");
        request.setGenero("M");
        request.setEdad(35);
        request.setIdentificacion("0912345678");
        request.setDireccion("Otavalo sn y principal");
        request.setTelefono("098254785");
        request.setClienteId("jose01");
        request.setContrasena("1234");
        request.setEstado(true);
        return request;
    }

    @Test
    @DisplayName("GET /api/clientes responde 200 con la lista de clientes")
    void listarClientesRetorna200ConLista() throws Exception {
        when(clienteService.listar()).thenReturn(List.of(clienteDto()));

        mockMvc.perform(get("/api/clientes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nombre").value("Jose Lema"))
            .andExpect(jsonPath("$[0].clienteId").value("jose01"));
    }

    @Test
    @DisplayName("POST /api/clientes con datos válidos responde 201")
    void crearClienteValidoRetorna201() throws Exception {
        when(clienteService.crear(any(ClienteRequest.class))).thenReturn(clienteDto());

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteRequest())))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.nombre").value("Jose Lema"));
    }

    @Test
    @DisplayName("POST /api/clientes sin nombre responde 400 con detalle de validación")
    void crearClienteSinNombreRetorna400() throws Exception {
        ClienteRequest request = clienteRequest();
        request.setNombre(null);

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.mensaje").value("Datos de entrada inválidos"))
            .andExpect(jsonPath("$.detalles[0]").value("El nombre es obligatorio"));
    }
}
