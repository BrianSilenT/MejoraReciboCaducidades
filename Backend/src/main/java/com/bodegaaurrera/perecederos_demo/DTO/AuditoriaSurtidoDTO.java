package com.bodegaaurrera.perecederos_demo.DTO;

import lombok.Data;

@Data
public class AuditoriaSurtidoDTO {

    private String codigoBarras;
    private String loteCorrecto;
    private String loteSurtido;
    private String usuario;
    private int cantidad;

    private boolean errorFEFO;

    private String mensaje;
}