package com.seguros.polizas.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "riesgos")
public class Riesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "direccion_inmueble", nullable = false)
    private String direccionInmueble;

    @Column(name = "matricula_inmobiliaria", nullable = false)
    private String matriculaInmobiliaria;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "valor_asegurado", precision = 14, scale = 2)
    private BigDecimal valorAsegurado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRiesgo estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poliza_id", nullable = false)
    @JsonBackReference
    private Poliza poliza;

    public Riesgo() {
        this.estado = EstadoRiesgo.ACTIVO;
    }

    public Riesgo(String direccionInmueble, String matriculaInmobiliaria, String descripcion, BigDecimal valorAsegurado) {
        this.direccionInmueble = direccionInmueble;
        this.matriculaInmobiliaria = matriculaInmobiliaria;
        this.descripcion = descripcion;
        this.valorAsegurado = valorAsegurado;
        this.estado = EstadoRiesgo.ACTIVO;
    }

    public void cancelar() {
        this.estado = EstadoRiesgo.CANCELADO;
    }

    // Getters y Setters
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

    public Poliza getPoliza() { return poliza; }
    public void setPoliza(Poliza poliza) { this.poliza = poliza; }
}
