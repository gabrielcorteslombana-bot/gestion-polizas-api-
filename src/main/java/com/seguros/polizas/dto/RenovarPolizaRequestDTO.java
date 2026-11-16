package com.seguros.polizas.dto;

import java.math.BigDecimal;

public class RenovarPolizaRequestDTO {

    /**
     * Factor de incremento por IPC. Por ejemplo, 0.05 representa un incremento del 5%.
     * Si no se suministra, el servicio aplicará el valor por defecto configurado (0.05).
     */
    private BigDecimal ipc;

    public RenovarPolizaRequestDTO() {}

    public RenovarPolizaRequestDTO(BigDecimal ipc) {
        this.ipc = ipc;
    }

    public BigDecimal getIpc() { return ipc; }
    public void setIpc(BigDecimal ipc) { this.ipc = ipc; }
}
