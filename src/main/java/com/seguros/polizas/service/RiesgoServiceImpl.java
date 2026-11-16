package com.seguros.polizas.service;

import com.seguros.polizas.dto.RiesgoResponseDTO;
import com.seguros.polizas.entity.EstadoRiesgo;
import com.seguros.polizas.entity.Riesgo;
import com.seguros.polizas.exception.BusinessException;
import com.seguros.polizas.exception.ResourceNotFoundException;
import com.seguros.polizas.repository.RiesgoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RiesgoServiceImpl implements RiesgoService {

    private static final Logger log = LoggerFactory.getLogger(RiesgoServiceImpl.class);

    private final RiesgoRepository riesgoRepository;
    private final CoreIntegrationService coreIntegrationService;

    public RiesgoServiceImpl(RiesgoRepository riesgoRepository,
                             CoreIntegrationService coreIntegrationService) {
        this.riesgoRepository = riesgoRepository;
        this.coreIntegrationService = coreIntegrationService;
    }

    @Override
    @Transactional(readOnly = true)
    public RiesgoResponseDTO obtenerPorId(Long id) {
        Riesgo riesgo = findRiesgoOrThrow(id);
        return RiesgoResponseDTO.fromEntity(riesgo);
    }

    @Override
    public RiesgoResponseDTO cancelarRiesgo(Long id) {
        Riesgo riesgo = findRiesgoOrThrow(id);

        if (riesgo.getEstado() == EstadoRiesgo.CANCELADO) {
            throw new BusinessException("El riesgo con ID " + id + " ya se encuentra CANCELADO.");
        }

        log.info("Cancelando riesgo con ID: {} perteneciente a la póliza ID: {}", id, riesgo.getPoliza().getId());
        riesgo.cancelar();

        Riesgo guardado = riesgoRepository.save(riesgo);

        // Notificación al servicio agnóstico del CORE (Capa media WebLogic)
        Long polizaId = (guardado.getPoliza() != null) ? guardado.getPoliza().getId() : 0L;
        coreIntegrationService.notificarCambioEstadoCore("ACTUALIZACION", polizaId);

        return RiesgoResponseDTO.fromEntity(guardado);
    }

    private Riesgo findRiesgoOrThrow(Long id) {
        return riesgoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el riesgo con ID: " + id));
    }
}
