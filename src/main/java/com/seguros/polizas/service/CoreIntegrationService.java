package com.seguros.polizas.service;

import com.seguros.polizas.dto.CoreEventoRequestDTO;
import com.seguros.polizas.dto.CoreEventoResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CoreIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(CoreIntegrationService.class);

    private final RestTemplate restTemplate;

    @Value("${app.core-integration.url:http://localhost:8080/core-mock/evento}")
    private String coreUrl;

    @Value("${app.core-integration.api-key:123456}")
    private String apiKey;

    public CoreIntegrationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void notificarCambioEstadoCore(String evento, Long polizaId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", apiKey);

            CoreEventoRequestDTO payload = new CoreEventoRequestDTO(evento, polizaId);
            HttpEntity<CoreEventoRequestDTO> entity = new HttpEntity<>(payload, headers);

            restTemplate.exchange(coreUrl, HttpMethod.POST, entity, CoreEventoResponseDTO.class);
            log.info("Notificación enviada a capa media WebLogic para póliza {}", polizaId);
        } catch (Exception ex) {
            log.warn("No fue posible sincronizar con el CORE en este momento: {}", ex.getMessage());
        }
    }
}
