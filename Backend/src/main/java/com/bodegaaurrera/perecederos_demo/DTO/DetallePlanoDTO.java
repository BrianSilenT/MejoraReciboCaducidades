package com.bodegaaurrera.perecederos_demo.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DetallePlanoDTO {
    private Long idDetalleCedis;
    private String producto;          // nombre del producto
    private Integer cantidadRecibida;
    private String lote;
    private LocalDate fechaCaducidad;
    private int cantidadRpc;
}