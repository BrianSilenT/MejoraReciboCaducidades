package com.bodegaaurrera.perecederos_demo.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class RecepcionCedisDTO {
    private Long idRecepcion;        // identificador único
    private String numeroCamion;     // número de camiones
    private String departamento;     // nombre del departamento (convertido desde Enum)
    private String division;         // nombre de la división
    private LocalDateTime fechaRegistro; // fecha/hora en que se creó la recepción
    private String estado;           // estado de la recepción (PARCIAL, FINALIZADA, etc.)
    private BigDecimal totalEsperado;       // total esperado según factura
    private BigDecimal totalRecibido;       // total recibido realmente
}