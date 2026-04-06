package com.bodegaaurrera.perecederos_demo.DTO;

import lombok.Data;

@Data
public class DiscrepanciaDTO {
    private Long idDiscrepancia;
    private int totalEsperado;   // 🔹 nuevo
    private int totalRecibido;   // 🔹 nuevo
    private int totalFaltante;
}
