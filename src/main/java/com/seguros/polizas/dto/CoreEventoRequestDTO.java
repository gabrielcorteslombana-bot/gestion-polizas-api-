package com.seguros.polizas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CoreEventoRequestDTO {

    @NotBlank(message = "El nombre del evento es obligatorio")
    private String evento;

    @NotNull(message = "El polizaId es obligatorio")
    private Long polizaId;

    public CoreEventoRequestDTO() {}

    public CoreEventoRequestDTO(String evento, Long polizaId) {
        this.evento = evento;
        this.polizaId = polizaId;
    }

    public String getEvento() { return evento; }
    public void setEvento(String evento) { this.evento = evento; }

    public Long getPolizaId() { return polizaId; }
    public void setPolizaId(Long polizaId) { this.polizaId = polizaId; }
}
