package com.bodegaaurrera.perecederos_demo.DTO;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder // Usar Builder es muy elegante para resúmenes
public class RpcResumenDTO {
    private long totalRegistradas;
    private long totalPendientes;
    private long totalCompletadas;
    private long totalEntregado;
    private long totalRetornado;
    private long totalFaltante;
}