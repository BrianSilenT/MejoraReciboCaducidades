package com.bodegaaurrera.perecederos_demo.Model;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_orden", nullable = false)
    private OrdenCompra ordenCompra;

    private int cantidad;
    private LocalDate fechaRecepcion;
    private LocalDate fechaCaducidad;
    private String lote;

    @Enumerated(EnumType.STRING)
    private EstadoRecepcion estado;
}
