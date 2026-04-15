package com.bodegaaurrera.perecederos_demo.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LoteDTO {

    private int cantidad;
    private String lote;
    private LocalDate fechaCaducidad;
    private long diasRestantes;
    private String estado; // ALERTA / OK
}