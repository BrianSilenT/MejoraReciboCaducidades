package com.bodegaaurrera.perecederos_demo.Model;

import com.bodegaaurrera.perecederos_demo.Enums.EstadoRecepcion;
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
@Table(name = "recepcion")
public class Recepcion {

    @OneToMany(mappedBy = "recepcion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecepcionDetalle> detalles;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRecepcion;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaRecepcion;

    @Enumerated(EnumType.STRING)
    private EstadoRecepcion estado;

    @ManyToOne
    @JoinColumn(name = "idOrden")
    private OrdenCompra ordenCompra;

    private String usuario;

    // 🔹 Relación con los productos recibidos
    @OneToMany(mappedBy = "recepcion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<RecepcionDetalle> productos;
}