package com.bodegaaurrera.perecederos_demo.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RecepcionCedisRequestDTO {

    @NotBlank(message = "El número de camión es obligatorio")
    private String numeroCamion;

    @NotNull(message = "El departamento es obligatorio")
    private String departamento; // lo recibes como String y luego lo conviertes a Enum

    @NotNull(message = "La división es obligatoria")
    private String division;

    @Min(value = 1, message = "El total esperado debe ser mayor a 0")
    private int totalEsperado;

    @NotNull(message = "Los detalles son obligatorios")
    private List<RecepcionCedisDetalleDTO> detalles;
}