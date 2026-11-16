package com.seguros.polizas.dto;

import com.seguros.polizas.entity.EstadoRiesgo;
import com.seguros.polizas.entity.Riesgo;

import java.math.BigDecimal;

public class RiesgoResponseDTO {

    private Long id;
    private String direccionInmueble;
    private String matriculaInmobiliaria;
    private String descripcion;
    private BigDecimal valorAsegurado;
    private EstadoRiesgo estado;
    private Long polizaId;

    public RiesgoResponseDTO() {}

    public static RiesgoResponseDTO fromEntity(Riesgo riesgo) {
        RiesgoResponseDTO dto = new RiesgoResponseDTO();
        dto.setId(riesgo.getId());
        dto.setDireccionInmueble(riesgo.getDireccionInmueble());
        dto.setMatriculaInmobiliaria(riesgo.getMatriculaInmobiliaria());
        dto.setDescripcion(riesgo.getDescripcion());
        dto.setValorAsegurado(riesgo.getValorAsegurado());
        dto.setEstado(riesgo.getEstado());
        if (riesgo.getPoliza() != null) {
            dto.setPolizaId(riesgo.getPoliza().getId());
        }
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDireccionInmueble() { return direccionInmueble; }
    public void setDireccionInmueble(String direccionInmueble) { this.direccionInmueble = direccionInmueble; }

    public String getMatriculaInmobiliaria() { return matriculaInmobiliaria; }
    public void setMatriculaInmobiliaria(String matriculaInmobiliaria) { this.matriculaInmobiliaria = matriculaInmobiliaria; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getValorAsegurado() { return valorAsegurado; }
    public void setValorAsegurado(BigDecimal valorAsegurado) { this.valorAsegurado = valorAsegurado; }

    public EstadoRiesgo getEstado() { return estado; }
    public void setEstado(EstadoRiesgo estado) { this.estado = estado; }

    public Long getPolizaId() { return polizaId; }
    public void setPolizaId(Long polizaId) { this.polizaId = polizaId; }
}
