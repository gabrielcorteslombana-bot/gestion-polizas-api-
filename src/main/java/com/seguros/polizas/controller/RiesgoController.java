package com.seguros.polizas.controller;

import com.seguros.polizas.dto.RiesgoResponseDTO;
import com.seguros.polizas.service.RiesgoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/riesgos")
@Tag(name = "Riesgos", description = "Operaciones de administración de riesgos")
public class RiesgoController {

    private final RiesgoService riesgoService;

    public RiesgoController(RiesgoService riesgoService) {
        this.riesgoService = riesgoService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar riesgo por ID")
    public ResponseEntity<RiesgoResponseDTO> obtenerRiesgoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(riesgoService.obtenerPorId(id));
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar un riesgo específico")
    public ResponseEntity<RiesgoResponseDTO> cancelarRiesgo(@PathVariable Long id) {
        return ResponseEntity.ok(riesgoService.cancelarRiesgo(id));
    }
}
