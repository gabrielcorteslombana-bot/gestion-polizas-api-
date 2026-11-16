package com.seguros.polizas.service;

import com.seguros.polizas.dto.*;
import com.seguros.polizas.entity.EstadoPoliza;
import com.seguros.polizas.entity.TipoPoliza;

import java.math.BigDecimal;
import java.util.List;

public interface PolizaService {

    List<PolizaResponseDTO> listarPolizas(TipoPoliza tipo, EstadoPoliza estado);

    PolizaResponseDTO obtenerPorId(Long id);

    PolizaResponseDTO crearPoliza(CrearPolizaRequestDTO request);

    List<RiesgoResponseDTO> obtenerRiesgosDePoliza(Long polizaId);

    PolizaResponseDTO renovarPoliza(Long id, BigDecimal factorIpc);

    PolizaResponseDTO cancelarPoliza(Long id);

    RiesgoResponseDTO agregarRiesgoAPoliza(Long polizaId, CrearRiesgoRequestDTO request);
}
