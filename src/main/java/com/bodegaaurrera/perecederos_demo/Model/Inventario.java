package com.bodegaaurrera.perecederos_demo.Model;

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

    private String codigoBarras;
    private String descripcion;
    private int cantidad;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaCaducidad;

    private String lote;

    private LocalDate fechaLlegada;   // se asigna automáticamente

    @Enumerated(EnumType.STRING)
    private Division division;

    @Enumerated(EnumType.STRING)
    private Departamento departamento;
}