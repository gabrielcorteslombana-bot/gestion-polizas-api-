package com.seguros.polizas.dto;

import java.time.LocalDateTime;

public class CoreEventoResponseDTO {

    private String status;
    private String mensaje;
    private String evento;
    private Long polizaId;
    private LocalDateTime timestamp;

    public CoreEventoResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public CoreEventoResponseDTO(String status, String mensaje, String evento, Long polizaId) {
        this.status = status;
        this.mensaje = mensaje;
        this.evento = evento;
        this.polizaId = polizaId;
        this.timestamp = LocalDateTime.now();
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getEvento() { return evento; }
    public void setEvento(String evento) { this.evento = evento; }

    public Long getPolizaId() { return polizaId; }
    public void setPolizaId(Long polizaId) { this.polizaId = polizaId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
