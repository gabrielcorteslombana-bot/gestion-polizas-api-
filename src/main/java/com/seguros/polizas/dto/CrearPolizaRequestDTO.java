package com.seguros.polizas.dto;

import com.seguros.polizas.entity.TipoPoliza;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CrearPolizaRequestDTO {

    @NotBlank(message = "El número de póliza es obligatorio")
    private String numeroPoliza;

    @NotNull(message = "El tipo de póliza es obligatorio (INDIVIDUAL o COLECTIVA)")
    private TipoPoliza tipo;

    @NotNull(message = "El canon mensual es obligatorio")
    @DecimalMin(value = "0.01", message = "El canon mensual debe ser mayor a cero")
    private BigDecimal canonMensual;

    @NotNull(message = "Los meses de vigencia son obligatorios")
    @Min(value = 1, message = "Los meses de vigencia deben ser al menos 1")
    private Integer mesesVigencia;

    @NotNull(message = "La fecha de inicio de vigencia es obligatoria")
    private LocalDate fechaInicioVigencia;

    @NotBlank(message = "El tomador es obligatorio")
    private String tomador;

    @NotBlank(message = "El asegurado es obligatorio")
    private String asegurado;

    @NotBlank(message = "El beneficiario es obligatorio")
    private String beneficiario;

    public CrearPolizaRequestDTO() {}

    public String getNumeroPoliza() { return numeroPoliza; }
    public void setNumeroPoliza(String numeroPoliza) { this.numeroPoliza = numeroPoliza; }

    public TipoPoliza getTipo() { return tipo; }
    public void setTipo(TipoPoliza tipo) { this.tipo = tipo; }

    public BigDecimal getCanonMensual() { return canonMensual; }
    public void setCanonMensual(BigDecimal canonMensual) { this.canonMensual = canonMensual; }

    public Integer getMesesVigencia() { return mesesVigencia; }
    public void setMesesVigencia(Integer mesesVigencia) { this.mesesVigencia = mesesVigencia; }

    public LocalDate getFechaInicioVigencia() { return fechaInicioVigencia; }
    public void setFechaInicioVigencia(LocalDate fechaInicioVigencia) { this.fechaInicioVigencia = fechaInicioVigencia; }

    public String getTomador() { return tomador; }
    public void setTomador(String tomador) { this.tomador = tomador; }

    public String getAsegurado() { return asegurado; }
    public void setAsegurado(String asegurado) { this.asegurado = asegurado; }

    public String getBeneficiario() { return beneficiario; }
    public void setBeneficiario(String beneficiario) { this.beneficiario = beneficiario; }
}
