package com.bodegaaurrera.perecederos_demo.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class InventarioDTO {
    private String codigoBarras;
    private String descripcion;
    private int cantidad;
    private String lote;
    private LocalDate fechaCaducidad;
}
