package com.seguros.polizas.controller;

import com.seguros.polizas.dto.*;
import com.seguros.polizas.entity.EstadoPoliza;
import com.seguros.polizas.entity.TipoPoliza;
import com.seguros.polizas.service.PolizaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/polizas")
@Tag(name = "Pólizas", description = "Operaciones de gestión y ciclo de vida de pólizas")
public class PolizaController {

    private final PolizaService polizaService;

    public PolizaController(PolizaService polizaService) {
        this.polizaService = polizaService;
    }

    @GetMapping
    @Operation(summary = "Listar pólizas con filtros opcionales de tipo y estado")
    public ResponseEntity<List<PolizaResponseDTO>> listarPolizas(
            @RequestParam(required = false) TipoPoliza tipo,
            @RequestParam(required = false) EstadoPoliza estado) {
        return ResponseEntity.ok(polizaService.listarPolizas(tipo, estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar póliza por ID")
    public ResponseEntity<PolizaResponseDTO> obtenerPolizaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear póliza (Individual o Colectiva)")
    public ResponseEntity<PolizaResponseDTO> crearPoliza(@Valid @RequestBody CrearPolizaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(polizaService.crearPoliza(request));
    }

    @GetMapping("/{id}/riesgos")
    @Operation(summary = "Listar riesgos asociados a una póliza")
    public ResponseEntity<List<RiesgoResponseDTO>> listarRiesgosDePoliza(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.obtenerRiesgosDePoliza(id));
    }

    @PostMapping("/{id}/renovar")
    @Operation(summary = "Renovar póliza aplicando incremento por IPC")
    public ResponseEntity<PolizaResponseDTO> renovarPoliza(
            @PathVariable Long id,
            @RequestBody(required = false) RenovarPolizaRequestDTO request) {
        BigDecimal factorIpc = (request != null) ? request.getIpc() : null;
        return ResponseEntity.ok(polizaService.renovarPoliza(id, factorIpc));
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar póliza y todos sus riesgos en cascada")
    public ResponseEntity<PolizaResponseDTO> cancelarPoliza(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.cancelarPoliza(id));
    }

    @PostMapping("/{id}/riesgos")
    @Operation(summary = "Agregar nuevo riesgo a una póliza colectiva")
    public ResponseEntity<RiesgoResponseDTO> agregarRiesgo(
            @PathVariable Long id,
            @Valid @RequestBody CrearRiesgoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(polizaService.agregarRiesgoAPoliza(id, request));
    }
}
