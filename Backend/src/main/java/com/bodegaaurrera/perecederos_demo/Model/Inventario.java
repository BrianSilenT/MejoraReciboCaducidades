package com.bodegaaurrera.perecederos_demo.Model;

import com.bodegaaurrera.perecederos_demo.Enums.Departamento;
import com.bodegaaurrera.perecederos_demo.Enums.Division;
import com.bodegaaurrera.perecederos_demo.Enums.Ubicacion;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "inventario")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInventario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    private int cantidad;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaCaducidad;

    private String lote;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaLlegada;   // se asigna automáticamente al registrar

    @Enumerated(EnumType.STRING)
    private Ubicacion ubicacion;

    @Enumerated(EnumType.STRING)
    private Division division;

    @Enumerated(EnumType.STRING)
    private Departamento departamento;
}