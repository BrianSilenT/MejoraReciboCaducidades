package com.bodegaaurrera.perecederos_demo.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RecepcionCedisDTO {
    private Long idRecepcion;        // identificador único
    private String numeroCamion;     // número de camión
    private String departamento;     // nombre del departamento (convertido desde Enum)
    private String division;         // nombre de la división
    private LocalDateTime fechaRegistro; // fecha/hora en que se creó la recepción
    private String estado;           // estado de la recepción (PARCIAL, FINALIZADA, etc.)
    private int totalEsperado;       // total esperado según factura
    private int totalRecibido;       // total recibido realmente
}