package com.seguros.polizas.service;

import com.seguros.polizas.dto.RiesgoResponseDTO;

public interface RiesgoService {

    RiesgoResponseDTO obtenerPorId(Long id);

    RiesgoResponseDTO cancelarRiesgo(Long id);
}
