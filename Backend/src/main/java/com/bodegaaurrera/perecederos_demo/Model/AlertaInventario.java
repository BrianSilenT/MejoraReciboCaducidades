package com.bodegaaurrera.perecederos_demo.Model;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AlertaInventario {
    private String codigoBarras;
    private String descripcion;
    private int cantidad;
    private LocalDate fechaCaducidad;
    private long diasRestantes;
    private String alerta;
    // getters y setters
}
