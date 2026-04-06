package com.bodegaaurrera.perecederos_demo.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "recepcion_detalle")
public class RecepcionDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalle;

    @ManyToOne
    @JoinColumn(name = "idRecepcion")
    @JsonBackReference
    private Recepcion recepcion;

    @ManyToOne
    @JoinColumn(name = "idProducto")
    private Producto producto;

    private int cantidadRecibida;
    private String lote;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaCaducidad;
}