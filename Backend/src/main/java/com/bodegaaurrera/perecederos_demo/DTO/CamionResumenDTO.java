package com.bodegaaurrera.perecederos_demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CamionResumenDTO {
    private String numeroCamion;
    private String departamento;
    private String estado;
}