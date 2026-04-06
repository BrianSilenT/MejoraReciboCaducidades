package com.bodegaaurrera.perecederos_demo.DTO;

import com.bodegaaurrera.perecederos_demo.Model.TipoRpc;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RecepcionCedisDetalleDTO {
    @NotNull(message = "El ID del producto es obligatorio")
    private Long idProducto;

    @Min(value = 0, message = "La cantidad recibida no puede ser negativa")
    private int cantidadRecibida;

    @Min(value = 1, message = "La cantidad esperada debe ser mayor a 0")
    private int cantidadEsperada; // 🔹 nuevo campo

    private String lote;

    private LocalDate fechaCaducidad;

    @Min(value = 0, message = "La cantidad de RPC no puede ser negativa")
    private Integer cantidadRpc; // ahora es objeto, puede ser null

    private TipoRpc tipoRpc; // en lugar de String
}