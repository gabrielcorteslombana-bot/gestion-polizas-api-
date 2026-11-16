package com.seguros.polizas.service;

import com.seguros.polizas.dto.*;
import com.seguros.polizas.entity.*;
import com.seguros.polizas.exception.BusinessException;
import com.seguros.polizas.exception.ResourceNotFoundException;
import com.seguros.polizas.repository.PolizaRepository;
import com.seguros.polizas.repository.RiesgoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PolizaServiceImpl implements PolizaService {

    private static final Logger log = LoggerFactory.getLogger(PolizaServiceImpl.class);

    private final PolizaRepository polizaRepository;
    private final RiesgoRepository riesgoRepository;
    private final CoreIntegrationService coreIntegrationService;

    @Value("${app.default-ipc:0.05}")
    private BigDecimal defaultIpc;

    public PolizaServiceImpl(PolizaRepository polizaRepository,
                             RiesgoRepository riesgoRepository,
                             CoreIntegrationService coreIntegrationService) {
        this.polizaRepository = polizaRepository;
        this.riesgoRepository = riesgoRepository;
        this.coreIntegrationService = coreIntegrationService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PolizaResponseDTO> listarPolizas(TipoPoliza tipo, EstadoPoliza estado) {
        return polizaRepository.findByFiltros(tipo, estado)
                .stream()
                .map(PolizaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PolizaResponseDTO obtenerPorId(Long id) {
        return PolizaResponseDTO.fromEntity(findPolizaOrThrow(id));
    }

    @Override
    public PolizaResponseDTO crearPoliza(CrearPolizaRequestDTO request) {
        if (polizaRepository.findByNumeroPoliza(request.getNumeroPoliza()).isPresent()) {
            throw new BusinessException("Ya existe una póliza registrada con el número: " + request.getNumeroPoliza());
        }

        Poliza poliza = new Poliza(
                request.getNumeroPoliza(),
                request.getTipo(),
                request.getCanonMensual(),
                request.getMesesVigencia(),
                request.getFechaInicioVigencia(),
                request.getTomador(),
                request.getAsegurado(),
                request.getBeneficiario()
        );

        Poliza guardada = polizaRepository.save(poliza);
        coreIntegrationService.notificarCambioEstadoCore("CREACION_POLIZA", guardada.getId());
        return PolizaResponseDTO.fromEntity(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiesgoResponseDTO> obtenerRiesgosDePoliza(Long polizaId) {
        findPolizaOrThrow(polizaId);
        return riesgoRepository.findByPolizaId(polizaId)
                .stream()
                .map(RiesgoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public PolizaResponseDTO renovarPoliza(Long id, BigDecimal factorIpc) {
        Poliza poliza = findPolizaOrThrow(id);

        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new BusinessException("No es posible renovar la póliza con ID " + id + " porque se encuentra CANCELADA.");
        }

        BigDecimal factor = (factorIpc != null && factorIpc.compareTo(BigDecimal.ZERO) > 0) ? factorIpc : defaultIpc;
        log.info("Renovando póliza {} con factor IPC {}", id, factor);

        poliza.renovar(factor);
        Poliza guardada = polizaRepository.save(poliza);

        coreIntegrationService.notificarCambioEstadoCore("ACTUALIZACION", guardada.getId());
        return PolizaResponseDTO.fromEntity(guardada);
    }

    @Override
    public PolizaResponseDTO cancelarPoliza(Long id) {
        Poliza poliza = findPolizaOrThrow(id);

        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new BusinessException("La póliza con ID " + id + " ya se encuentra en estado CANCELADA.");
        }

        log.info("Cancelando póliza {} y sus riesgos asociados", id);
        poliza.cancelar();
        Poliza guardada = polizaRepository.save(poliza);

        coreIntegrationService.notificarCambioEstadoCore("ACTUALIZACION", guardada.getId());
        return PolizaResponseDTO.fromEntity(guardada);
    }

    @Override
    public RiesgoResponseDTO agregarRiesgoAPoliza(Long polizaId, CrearRiesgoRequestDTO request) {
        Poliza poliza = findPolizaOrThrow(polizaId);

        if (poliza.getTipo() != TipoPoliza.COLECTIVA) {
            throw new BusinessException("No se pueden agregar riesgos adicionales: La póliza ID " + polizaId +
                    " es de tipo " + poliza.getTipo() + ". Solo las pólizas COLECTIVAS admiten agregar nuevos riesgos.");
        }

        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new BusinessException("No se pueden asociar riesgos a una póliza en estado CANCELADA.");
        }

        Riesgo nuevoRiesgo = new Riesgo(
                request.getDireccionInmueble(),
                request.getMatriculaInmobiliaria(),
                request.getDescripcion(),
                request.getValorAsegurado()
        );

        poliza.agregarRiesgo(nuevoRiesgo);
        Riesgo riesgoGuardado = riesgoRepository.save(nuevoRiesgo);

        coreIntegrationService.notificarCambioEstadoCore("ACTUALIZACION", poliza.getId());
        return RiesgoResponseDTO.fromEntity(riesgoGuardado);
    }

    private Poliza findPolizaOrThrow(Long id) {
        return polizaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la póliza con ID: " + id));
    }
}
