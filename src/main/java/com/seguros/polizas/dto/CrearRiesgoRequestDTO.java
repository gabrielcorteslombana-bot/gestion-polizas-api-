package com.seguros.polizas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CrearRiesgoRequestDTO {

    @NotBlank(message = "La dirección del inmueble es obligatoria")
    private String direccionInmueble;

    @NotBlank(message = "La matrícula inmobiliaria es obligatoria")
    private String matriculaInmobiliaria;

    private String descripcion;

    @NotNull(message = "El valor asegurado es obligatorio")
    @DecimalMin(value = "0.01", message = "El valor asegurado debe ser mayor a cero")
    private BigDecimal valorAsegurado;

    public CrearRiesgoRequestDTO() {}

    public String getDireccionInmueble() { return direccionInmueble; }
    public void setDireccionInmueble(String direccionInmueble) { this.direccionInmueble = direccionInmueble; }

    public String getMatriculaInmobiliaria() { return matriculaInmobiliaria; }
    public void setMatriculaInmobiliaria(String matriculaInmobiliaria) { this.matriculaInmobiliaria = matriculaInmobiliaria; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getValorAsegurado() { return valorAsegurado; }
    public void setValorAsegurado(BigDecimal valorAsegurado) { this.valorAsegurado = valorAsegurado; }
}
