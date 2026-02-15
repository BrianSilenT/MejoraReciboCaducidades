package com.bodegaaurrera.perecederos_demo.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "recepcion")
public class Recepcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRecepcion;

    private String lote;
    private int cantidad;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaRecepcion;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaCaducidad;

    @Enumerated(EnumType.STRING)
    private EstadoRecepcion estado;

    @ManyToOne
    @JoinColumn(name = "idOrden")
    private OrdenCompra ordenCompra;

    @ManyToOne
    @JoinColumn(name = "idProducto")
    private Producto producto;
}