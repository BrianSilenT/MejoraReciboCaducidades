package com.bodegaaurrera.perecederos_demo.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RpcDTO {
    private Long idRpc;
    private String tipoRpc;
    private int cantidadEntregada;
    private int cantidadRetornada;
    private boolean pendienteRetorno;
    private LocalDate fechaRegistro;
}