package com.bodegaaurrera.perecederos_demo.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MovimientoInventarioDTO {

    private String upc;
    private List<LoteMovimientoDTO> lotes;
}