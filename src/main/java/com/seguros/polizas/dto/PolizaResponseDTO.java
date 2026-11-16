package com.seguros.polizas.dto;

import com.seguros.polizas.entity.EstadoPoliza;
import com.seguros.polizas.entity.Poliza;
import com.seguros.polizas.entity.TipoPoliza;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class PolizaResponseDTO {

    private Long id;
    private String numeroPoliza;
    private TipoPoliza tipo;
    private EstadoPoliza estado;
    private BigDecimal canonMensual;
    private Integer mesesVigencia;
    private BigDecimal valorPrima;
    private LocalDate fechaInicioVigencia;
    private LocalDate fechaFinVigencia;
    private String tomador;
    private String asegurado;
    private String beneficiario;
    private List<RiesgoResponseDTO> riesgos;

    public PolizaResponseDTO() {}

    public static PolizaResponseDTO fromEntity(Poliza poliza) {
        PolizaResponseDTO dto = new PolizaResponseDTO();
        dto.setId(poliza.getId());
        dto.setNumeroPoliza(poliza.getNumeroPoliza());
        dto.setTipo(poliza.getTipo());
        dto.setEstado(poliza.getEstado());
        dto.setCanonMensual(poliza.getCanonMensual());
        dto.setMesesVigencia(poliza.getMesesVigencia());
        dto.setValorPrima(poliza.getValorPrima());
        dto.setFechaInicioVigencia(poliza.getFechaInicioVigencia());
        dto.setFechaFinVigencia(poliza.getFechaFinVigencia());
        dto.setTomador(poliza.getTomador());
        dto.setAsegurado(poliza.getAsegurado());
        dto.setBeneficiario(poliza.getBeneficiario());
        if (poliza.getRiesgos() != null) {
            dto.setRiesgos(poliza.getRiesgos().stream()
                    .map(RiesgoResponseDTO::fromEntity)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroPoliza() { return numeroPoliza; }
    public void setNumeroPoliza(String numeroPoliza) { this.numeroPoliza = numeroPoliza; }

    public TipoPoliza getTipo() { return tipo; }
    public void setTipo(TipoPoliza tipo) { this.tipo = tipo; }

    public EstadoPoliza getEstado() { return estado; }
    public void setEstado(EstadoPoliza estado) { this.estado = estado; }

    public BigDecimal getCanonMensual() { return canonMensual; }
    public void setCanonMensual(BigDecimal canonMensual) { this.canonMensual = canonMensual; }

    public Integer getMesesVigencia() { return mesesVigencia; }
    public void setMesesVigencia(Integer mesesVigencia) { this.mesesVigencia = mesesVigencia; }

    public BigDecimal getValorPrima() { return valorPrima; }
    public void setValorPrima(BigDecimal valorPrima) { this.valorPrima = valorPrima; }

    public LocalDate getFechaInicioVigencia() { return fechaInicioVigencia; }
    public void setFechaInicioVigencia(LocalDate fechaInicioVigencia) { this.fechaInicioVigencia = fechaInicioVigencia; }

    public LocalDate getFechaFinVigencia() { return fechaFinVigencia; }
    public void setFechaFinVigencia(LocalDate fechaFinVigencia) { this.fechaFinVigencia = fechaFinVigencia; }

    public String getTomador() { return tomador; }
    public void setTomador(String tomador) { this.tomador = tomador; }

    public String getAsegurado() { return asegurado; }
    public void setAsegurado(String asegurado) { this.asegurado = asegurado; }

    public String getBeneficiario() { return beneficiario; }
    public void setBeneficiario(String beneficiario) { this.beneficiario = beneficiario; }

    public List<RiesgoResponseDTO> getRiesgos() { return riesgos; }
    public void setRiesgos(List<RiesgoResponseDTO> riesgos) { this.riesgos = riesgos; }
}
