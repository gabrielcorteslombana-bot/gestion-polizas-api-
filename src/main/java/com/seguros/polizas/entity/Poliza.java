package com.seguros.polizas.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polizas")
public class Poliza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_poliza", nullable = false, unique = true)
    private String numeroPoliza;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPoliza tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPoliza estado;

    @Column(name = "canon_mensual", nullable = false, precision = 14, scale = 2)
    private BigDecimal canonMensual;

    @Column(name = "meses_vigencia", nullable = false)
    private Integer mesesVigencia;

    @Column(name = "valor_prima", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorPrima;

    @Column(name = "fecha_inicio_vigencia", nullable = false)
    private LocalDate fechaInicioVigencia;

    @Column(name = "fecha_fin_vigencia", nullable = false)
    private LocalDate fechaFinVigencia;

    @Column(name = "tomador", nullable = false)
    private String tomador;

    @Column(name = "asegurado", nullable = false)
    private String asegurado;

    @Column(name = "beneficiario", nullable = false)
    private String beneficiario;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Riesgo> riesgos = new ArrayList<>();

    public Poliza() {
        this.estado = EstadoPoliza.ACTIVA;
    }

    public Poliza(String numeroPoliza, TipoPoliza tipo, BigDecimal canonMensual, Integer mesesVigencia,
                  LocalDate fechaInicioVigencia, String tomador, String asegurado, String beneficiario) {
        this.numeroPoliza = numeroPoliza;
        this.tipo = tipo;
        this.estado = EstadoPoliza.ACTIVA;
        this.canonMensual = canonMensual;
        this.mesesVigencia = mesesVigencia;
        this.fechaInicioVigencia = fechaInicioVigencia;
        this.fechaFinVigencia = fechaInicioVigencia.plusMonths(mesesVigencia);
        this.tomador = tomador;
        this.asegurado = asegurado;
        this.beneficiario = beneficiario;
        this.valorPrima = calcularPrima(canonMensual, mesesVigencia);
    }

    public BigDecimal calcularPrima(BigDecimal canon, int meses) {
        if (canon == null || meses <= 0) {
            return BigDecimal.ZERO;
        }
        return canon.multiply(BigDecimal.valueOf(meses)).setScale(2, RoundingMode.HALF_UP);
    }

    public void renovar(BigDecimal factorIpc) {
        if (this.estado == EstadoPoliza.CANCELADA) {
            throw new IllegalStateException("No se puede renovar una póliza que se encuentra en estado CANCELADA.");
        }

        BigDecimal multiplicador = BigDecimal.ONE.add(factorIpc);
        this.canonMensual = this.canonMensual.multiply(multiplicador).setScale(2, RoundingMode.HALF_UP);
        this.valorPrima = calcularPrima(this.canonMensual, this.mesesVigencia);

        this.fechaInicioVigencia = this.fechaFinVigencia;
        this.fechaFinVigencia = this.fechaInicioVigencia.plusMonths(this.mesesVigencia);
        this.estado = EstadoPoliza.RENOVADA;
    }

    public void cancelar() {
        this.estado = EstadoPoliza.CANCELADA;
        if (this.riesgos != null) {
            for (Riesgo riesgo : this.riesgos) {
                if (riesgo.getEstado() != EstadoRiesgo.CANCELADO) {
                    riesgo.setEstado(EstadoRiesgo.CANCELADO);
                }
            }
        }
    }

    public void agregarRiesgo(Riesgo riesgo) {
        if (this.estado == EstadoPoliza.CANCELADA) {
            throw new IllegalStateException("No se pueden agregar riesgos a una póliza cancelada.");
        }
        if (this.tipo == TipoPoliza.INDIVIDUAL && !this.riesgos.isEmpty()) {
            throw new IllegalStateException("Una póliza individual solo puede tener 1 riesgo.");
        }
        this.riesgos.add(riesgo);
        riesgo.setPoliza(this);
    }

    // Getters y Setters
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

    public List<Riesgo> getRiesgos() { return riesgos; }
    public void setRiesgos(List<Riesgo> riesgos) { this.riesgos = riesgos; }
}
