package com.bodegaaurrera.perecederos_demo.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoteMovimientoDTO {

    private String lote;
    private BigDecimal cantidad;
}