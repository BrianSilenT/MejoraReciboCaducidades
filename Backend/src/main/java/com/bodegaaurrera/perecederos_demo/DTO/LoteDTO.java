package com.bodegaaurrera.perecederos_demo.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class LoteDTO {

    private BigDecimal cantidad;
    private String lote;
    private LocalDate fechaCaducidad;
    private long diasRestantes;
    private String estado; // ALERTA / OK
}