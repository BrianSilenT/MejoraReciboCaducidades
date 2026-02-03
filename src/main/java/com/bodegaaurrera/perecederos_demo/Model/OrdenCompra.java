package com.bodegaaurrera.perecederos_demo.Model;

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

    private String numeroOrden;

    private LocalDate fechaEmision;

    private int vigenciaDias;

    private int cantidadSolicitada;

    private String proveedor;

    @Enumerated(EnumType.STRING)
    private EstadoOrden estado;

    // Relación inversa: una orden puede tener varias recepciones
    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL)
    private List<Recepcion> recepciones;;
}