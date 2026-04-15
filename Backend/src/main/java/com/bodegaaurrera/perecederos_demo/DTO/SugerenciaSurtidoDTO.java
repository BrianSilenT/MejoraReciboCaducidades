package com.bodegaaurrera.perecederos_demo.DTO;


import com.bodegaaurrera.perecederos_demo.Enums.TipoAlerta;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SugerenciaSurtidoDTO {

    private String codigoBarras;
    private String descripcion;

    private int cantidadSolicitada;

    private TipoAlerta tipo;
    // URGENTE / SUGERENCIA / OK

    private List<LoteDTO> lotesSugeridos;
}
