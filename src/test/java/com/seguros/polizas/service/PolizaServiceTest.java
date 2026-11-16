package com.seguros.polizas.service;

import com.seguros.polizas.dto.CrearRiesgoRequestDTO;
import com.seguros.polizas.dto.PolizaResponseDTO;
import com.seguros.polizas.dto.RiesgoResponseDTO;
import com.seguros.polizas.entity.*;
import com.seguros.polizas.exception.BusinessException;
import com.seguros.polizas.repository.PolizaRepository;
import com.seguros.polizas.repository.RiesgoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolizaServiceTest {

    @Mock
    private PolizaRepository polizaRepository;

    @Mock
    private RiesgoRepository riesgoRepository;

    @Mock
    private CoreIntegrationService coreIntegrationService;

    @InjectMocks
    private PolizaServiceImpl polizaService;

    private Poliza polizaIndividual;
    private Poliza polizaColectiva;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(polizaService, "defaultIpc", new BigDecimal("0.05"));

        polizaIndividual = new Poliza(
                "POL-IND-001",
                TipoPoliza.INDIVIDUAL,
                new BigDecimal("1000000.00"),
                12,
                LocalDate.now(),
                "Tomador Juan",
                "Asegurado Juan",
                "Beneficiario Pedro"
        );
        polizaIndividual.setId(1L);

        polizaColectiva = new Poliza(
                "POL-COL-001",
                TipoPoliza.COLECTIVA,
                new BigDecimal("5000000.00"),
                12,
                LocalDate.now(),
                "Inmobiliaria Central",
                "Arrendatarios Varios",
                "Propietarios Varios"
        );
        polizaColectiva.setId(2L);
    }

    @Test
    @DisplayName("Regla 1: Renovar póliza incrementa canon y prima en +IPC y estado pasa a RENOVADA")
    void testRenovarPolizaExitosa() {
        when(polizaRepository.findById(1L)).thenReturn(Optional.of(polizaIndividual));
        when(polizaRepository.save(any(Poliza.class))).thenAnswer(inv -> inv.getArgument(0));

        BigDecimal ipc = new BigDecimal("0.10"); // 10% IPC
        PolizaResponseDTO renovada = polizaService.renovarPoliza(1L, ipc);

        assertNotNull(renovada);
        assertEquals(EstadoPoliza.RENOVADA, renovada.getEstado());
        // Canon previo: 1,000,000 * 1.10 = 1,100,000.00
        assertEquals(new BigDecimal("1100000.00"), renovada.getCanonMensual());
        // Prima previa: 12,000,000 -> Nueva: 1,100,000 * 12 = 13,200,000.00
        assertEquals(new BigDecimal("13200000.00"), renovada.getValorPrima());

        verify(coreIntegrationService, times(1)).notificarCambioEstadoCore(eq("ACTUALIZACION"), eq(1L));
        verify(polizaRepository, times(1)).save(any(Poliza.class));
    }

    @Test
    @DisplayName("Regla 2: No se puede renovar una póliza cancelada")
    void testNoSePuedeRenovarPolizaCancelada() {
        polizaIndividual.setEstado(EstadoPoliza.CANCELADA);
        when(polizaRepository.findById(1L)).thenReturn(Optional.of(polizaIndividual));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                polizaService.renovarPoliza(1L, new BigDecimal("0.05"))
        );

        assertTrue(exception.getMessage().contains("se encuentra CANCELADA"));
        verify(polizaRepository, never()).save(any(Poliza.class));
        verify(coreIntegrationService, never()).notificarCambioEstadoCore(any(), any());
    }

    @Test
    @DisplayName("Regla 3: La cancelación de una póliza cancela todos sus riesgos")
    void testCancelarPolizaCancelaTodosLosRiesgos() {
        Riesgo riesgo1 = new Riesgo("Calle 100", "MAT-1", "Oficina", new BigDecimal("100000000"));
        riesgo1.setId(10L);
        polizaColectiva.agregarRiesgo(riesgo1);

        Riesgo riesgo2 = new Riesgo("Calle 101", "MAT-2", "Local", new BigDecimal("200000000"));
        riesgo2.setId(11L);
        polizaColectiva.agregarRiesgo(riesgo2);

        when(polizaRepository.findById(2L)).thenReturn(Optional.of(polizaColectiva));
        when(polizaRepository.save(any(Poliza.class))).thenAnswer(inv -> inv.getArgument(0));

        PolizaResponseDTO cancelada = polizaService.cancelarPoliza(2L);

        assertEquals(EstadoPoliza.CANCELADA, cancelada.getEstado());
        assertEquals(EstadoRiesgo.CANCELADO, riesgo1.getEstado());
        assertEquals(EstadoRiesgo.CANCELADO, riesgo2.getEstado());

        verify(coreIntegrationService, times(1)).notificarCambioEstadoCore(eq("ACTUALIZACION"), eq(2L));
    }

    @Test
    @DisplayName("Regla 4: Agregar riesgo exige validación de tipo de póliza (Solo COLECTIVA)")
    void testAgregarRiesgoAPolizaIndividualFalla() {
        when(polizaRepository.findById(1L)).thenReturn(Optional.of(polizaIndividual));

        CrearRiesgoRequestDTO request = new CrearRiesgoRequestDTO();
        request.setDireccionInmueble("Calle 50 # 10-20");
        request.setMatriculaInmobiliaria("MAT-999");
        request.setValorAsegurado(new BigDecimal("150000000"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                polizaService.agregarRiesgoAPoliza(1L, request)
        );

        assertTrue(exception.getMessage().contains("Solo las pólizas COLECTIVAS admiten agregar nuevos riesgos"));
        verify(riesgoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Regla 5: Agregar riesgo a póliza colectiva exitoso")
    void testAgregarRiesgoAPolizaColectivaExitoso() {
        when(polizaRepository.findById(2L)).thenReturn(Optional.of(polizaColectiva));
        when(riesgoRepository.save(any(Riesgo.class))).thenAnswer(inv -> {
            Riesgo r = inv.getArgument(0);
            r.setId(99L);
            return r;
        });

        CrearRiesgoRequestDTO request = new CrearRiesgoRequestDTO();
        request.setDireccionInmueble("Avenida El Dorado # 68C");
        request.setMatriculaInmobiliaria("MAT-COL-55");
        request.setValorAsegurado(new BigDecimal("350000000"));
        request.setDescripcion("Bodega industrial");

        RiesgoResponseDTO respuesta = polizaService.agregarRiesgoAPoliza(2L, request);

        assertNotNull(respuesta);
        assertEquals(99L, respuesta.getId());
        assertEquals(EstadoRiesgo.ACTIVO, respuesta.getEstado());
        verify(riesgoRepository, times(1)).save(any(Riesgo.class));
        verify(coreIntegrationService, times(1)).notificarCambioEstadoCore(eq("ACTUALIZACION"), eq(2L));
    }
}
