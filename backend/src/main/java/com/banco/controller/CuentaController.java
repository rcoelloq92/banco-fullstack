package com.banco.controller;

import com.banco.dto.CuentaDto;
import com.banco.dto.CuentaRequest;
import com.banco.service.CuentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;

    @GetMapping
    public ResponseEntity<List<CuentaDto>> listar() {
        return ResponseEntity.ok(cuentaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaDto> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(cuentaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<CuentaDto> crear(@Valid @RequestBody CuentaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuentaDto> actualizar(@PathVariable Long id,
                                                @Valid @RequestBody CuentaRequest request) {
        return ResponseEntity.ok(cuentaService.actualizar(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CuentaDto> actualizarParcial(@PathVariable Long id,
                                                       @RequestBody CuentaRequest request) {
        return ResponseEntity.ok(cuentaService.actualizarParcial(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cuentaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
