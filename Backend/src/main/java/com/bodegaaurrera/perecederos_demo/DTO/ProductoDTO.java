package com.bodegaaurrera.perecederos_demo.DTO;

import lombok.Data;

@Data
public class ProductoDTO {
    private Long id;
    private String nombre;
    private String codigoBarras;
    private String descripcion;
    private String departamento;
    private String division;
    private Double precio;
}