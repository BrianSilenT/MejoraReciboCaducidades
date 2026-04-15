package com.bodegaaurrera.perecederos_demo.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orden_compra_detalle")
public class OrdenCompraDetalle {


    @JsonManagedReference
    @OneToMany(mappedBy = "ordenCompraDetalle")
    private List<RecepcionDetalle> detalles;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalle;

    @ManyToOne
    @JoinColumn(name = "id_Orden")
    @JsonBackReference
    private OrdenCompra ordenCompra;

    @ManyToOne
    @JoinColumn(name = "id_Producto")
    private Producto producto;

    private Integer cantidadRecibida;
    private int cantidadEsperada;
}