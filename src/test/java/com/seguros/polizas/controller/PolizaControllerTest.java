package com.seguros.polizas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seguros.polizas.dto.CoreEventoRequestDTO;
import com.seguros.polizas.dto.PolizaResponseDTO;
import com.seguros.polizas.entity.EstadoPoliza;
import com.seguros.polizas.entity.TipoPoliza;
import com.seguros.polizas.service.PolizaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({PolizaController.class, CoreMockController.class})
class PolizaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PolizaService polizaService;

    @Test
    @DisplayName("Endpoint 1: GET /polizas retorna lista de pólizas")
    void testListarPolizas() throws Exception {
        PolizaResponseDTO dto = new PolizaResponseDTO();
        dto.setId(1L);
        dto.setNumeroPoliza("POL-IND-001");
        dto.setTipo(TipoPoliza.INDIVIDUAL);
        dto.setEstado(EstadoPoliza.ACTIVA);

        when(polizaService.listarPolizas(any(), any())).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/polizas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numeroPoliza").value("POL-IND-001"))
                .andExpect(jsonPath("$[0].tipo").value("INDIVIDUAL"));
    }

    @Test
    @DisplayName("Endpoint 3: POST /polizas/{id}/renovar exitoso")
    void testRenovarPoliza() throws Exception {
        PolizaResponseDTO dto = new PolizaResponseDTO();
        dto.setId(1L);
        dto.setNumeroPoliza("POL-IND-001");
        dto.setTipo(TipoPoliza.INDIVIDUAL);
        dto.setEstado(EstadoPoliza.RENOVADA);
        dto.setCanonMensual(new BigDecimal("1100000.00"));

        when(polizaService.renovarPoliza(eq(1L), any())).thenReturn(dto);

        mockMvc.perform(post("/polizas/1/renovar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ipc\": 0.10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RENOVADA"))
                .andExpect(jsonPath("$.canonMensual").value(1100000.00));
    }

    @Test
    @DisplayName("Mock CORE: POST /core-mock/evento sin header x-api-key retorna 401 Unauthorized")
    void testCoreMockSinApiKeyRetorna401() throws Exception {
        CoreEventoRequestDTO request = new CoreEventoRequestDTO("ACTUALIZACION", 555L);

        mockMvc.perform(post("/core-mock/evento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Mock CORE: POST /core-mock/evento con header x-api-key correcto retorna 200 OK")
    void testCoreMockConApiKeyRetorna200() throws Exception {
        CoreEventoRequestDTO request = new CoreEventoRequestDTO("ACTUALIZACION", 555L);

        mockMvc.perform(post("/core-mock/evento")
                        .header("x-api-key", "123456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.evento").value("ACTUALIZACION"))
                .andExpect(jsonPath("$.polizaId").value(555));
    }
}
