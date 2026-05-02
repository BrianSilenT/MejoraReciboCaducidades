package com.bodegaaurrera.perecederos_demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@Data
public class EtiquetaRequestDTO {

    private String upc;
    private String lote;
    private LocalDate fechaCaducidad;

    // Para piezas (cajas)
    private Integer cantidadPorCaja; // opcional
    private Integer numeroEtiquetas; // cuántas imprimir
    private BigDecimal cantidadTotal; //  coincide
    private int numeroCajas;

    // Para granel
    private BigDecimal pesoTotal; // opcional

    // Impresora
    private String ip;
    private Integer puerto = 9100;
}