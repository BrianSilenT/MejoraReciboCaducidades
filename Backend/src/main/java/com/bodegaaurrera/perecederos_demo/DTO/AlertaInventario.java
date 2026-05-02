package com.bodegaaurrera.perecederos_demo.DTO;


import com.bodegaaurrera.perecederos_demo.Enums.TipoAlerta;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class AlertaInventario {

    private String codigoBarras;
    private String descripcion;

    private BigDecimal cantidad;
    private LocalDate fechaCaducidad;
    private String lote;

    private long diasRestantes;

    private TipoAlerta tipo;
    // getters y setters
}
