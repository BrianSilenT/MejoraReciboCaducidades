package com.bodegaaurrera.perecederos_demo.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DiscrepanciaDTO {
    private Long idDiscrepancia;
    private BigDecimal totalEsperado;   // 🔹 nuevo
    private BigDecimal totalRecibido;   // 🔹 nuevo
    private BigDecimal totalFaltante;
}
