package com.seguros.polizas.controller;

import com.seguros.polizas.dto.CoreEventoRequestDTO;
import com.seguros.polizas.dto.CoreEventoResponseDTO;
import com.seguros.polizas.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/core-mock")
@Tag(name = "CORE Mock", description = "Mock de sincronización hacia capa media WebLogic del CORE")
public class CoreMockController {

    private static final Logger log = LoggerFactory.getLogger(CoreMockController.class);

    @Value("${app.core-integration.api-key:123456}")
    private String expectedApiKey;

    @PostMapping("/evento")
    @Operation(summary = "Recepción de eventos de sincronización para el CORE")
    public ResponseEntity<?> recibirEventoCore(
            @RequestHeader(value = "x-api-key", required = false) String apiKey,
            @Valid @RequestBody CoreEventoRequestDTO request) {

        if (apiKey == null || !expectedApiKey.equals(apiKey)) {
            log.warn("Rechazada petición al mock del CORE: apiKey ausente o inválida");
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Unauthorized",
                    "Cabecera x-api-key no suministrada o inválida",
                    "/core-mock/evento"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        log.info("[CORE-SYNC] Notificación recibida en capa media WebLogic -> Evento: '{}', PolizaId: {}",
                request.getEvento(), request.getPolizaId());

        CoreEventoResponseDTO response = new CoreEventoResponseDTO(
                "SUCCESS",
                "Evento registrado correctamente en el sistema CORE",
                request.getEvento(),
                request.getPolizaId()
        );

        return ResponseEntity.ok(response);
    }
}
