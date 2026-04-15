package com.bodegaaurrera.perecederos_demo.Model;

import com.bodegaaurrera.perecederos_demo.Enums.EstadoOrden;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orden_compra")
public class OrdenCompra {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOrden;



    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdenCompraDetalle> detalles;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaEmision;
    private int vigenciaDias;


    @Enumerated(EnumType.STRING)
    private EstadoOrden estado;


}