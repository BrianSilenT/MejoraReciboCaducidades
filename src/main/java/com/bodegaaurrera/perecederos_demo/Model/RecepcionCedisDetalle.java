package com.bodegaaurrera.perecederos_demo.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "recepcion_cedis_detalle")
public class RecepcionCedisDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalleCedis;

    @ManyToOne
    @JoinColumn(name = "idRecepcionCedis")
    @JsonIgnore
    private RecepcionCedis recepcionCedis;

    @ManyToOne
    @JoinColumn(name = "idProducto")
    private Producto producto;

    private int cantidadEsperada;   // 🔹 lo que CEDIS mandó
    private int cantidadRecibida;   // 🔹 lo que auditor captura
    private String lote;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaCaducidad;

    private Integer cantidadRpc; // opcional para frutas/verduras

}