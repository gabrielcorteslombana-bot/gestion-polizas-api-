package com.seguros.polizas.config;

import com.seguros.polizas.entity.*;
import com.seguros.polizas.repository.PolizaRepository;
import com.seguros.polizas.repository.RiesgoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final PolizaRepository polizaRepository;
    private final RiesgoRepository riesgoRepository;

    public DataInitializer(PolizaRepository polizaRepository, RiesgoRepository riesgoRepository) {
        this.polizaRepository = polizaRepository;
        this.riesgoRepository = riesgoRepository;
    }

    @Override
    public void run(String... args) {
        log.info("Inicializando datos semilla para pruebas técnicas...");

        // 1. Póliza Colectiva para Inmobiliaria con 2 riesgos iniciales
        Poliza polizaColectiva = new Poliza(
                "POL-COL-1001",
                TipoPoliza.COLECTIVA,
                new BigDecimal("4500000.00"), // Canon mensual conjunto
                12, // 12 meses
                LocalDate.now(),
                "Inmobiliaria El Libertador S.A.S.",
                "Copropiedad Torre Empresarial 100",
                "Fiduciaria Central"
        );
        polizaRepository.save(polizaColectiva);

        Riesgo riesgo1 = new Riesgo(
                "Carrera 7 # 120 - 45 Oficina 501, Bogotá",
                "50C-1234567",
                "Oficina comercial consultorio",
                new BigDecimal("800000000.00")
        );
        polizaColectiva.agregarRiesgo(riesgo1);
        riesgoRepository.save(riesgo1);

        Riesgo riesgo2 = new Riesgo(
                "Carrera 7 # 120 - 45 Local 102, Bogotá",
                "50C-1234568",
                "Local comercial bancario",
                new BigDecimal("1200000000.00")
        );
        polizaColectiva.agregarRiesgo(riesgo2);
        riesgoRepository.save(riesgo2);

        // 2. Póliza Individual con 1 único riesgo
        Poliza polizaIndividual = new Poliza(
                "POL-IND-2001",
                TipoPoliza.INDIVIDUAL,
                new BigDecimal("2200000.00"), // Canon mensual
                12, // 12 meses
                LocalDate.now().minusMonths(11), // Por vencer
                "Carlos Alberto Restrepo (Arrendatario/Tomador)",
                "Carlos Alberto Restrepo (Asegurado)",
                "María Elena Gómez (Arrendador/Beneficiaria)"
        );
        polizaRepository.save(polizaIndividual);

        Riesgo riesgoInd = new Riesgo(
                "Calle 134 # 19 - 25 Apto 402, Bogotá",
                "50N-9876543",
                "Apartamento residencial",
                new BigDecimal("450000000.00")
        );
        polizaIndividual.agregarRiesgo(riesgoInd);
        riesgoRepository.save(riesgoInd);

        // 3. Póliza Individual ya Cancelada (para validar regla de negocio de rechazo de renovación)
        Poliza polizaCancelada = new Poliza(
                "POL-IND-3001",
                TipoPoliza.INDIVIDUAL,
                new BigDecimal("1800000.00"),
                6,
                LocalDate.now().minusMonths(8),
                "Pedro Picapiedra",
                "Pedro Picapiedra",
                "Vilma Palma"
        );
        polizaCancelada.setEstado(EstadoPoliza.CANCELADA);
        polizaRepository.save(polizaCancelada);

        log.info("Datos semilla cargados exitosamente: 3 Pólizas (1 Colectiva, 1 Individual Activa, 1 Individual Cancelada).");
    }
}
