package com.bodegaaurrera.perecederos_demo.Model;

import com.bodegaaurrera.perecederos_demo.Enums.Departamento;
import com.bodegaaurrera.perecederos_demo.Enums.Division;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProducto;

    @NotBlank
    private String nombre;

    @Column(unique = true)
    @NotBlank
    private String codigoBarras;
    private Double precio;

    @Enumerated(EnumType.STRING)
    private Departamento departamento;

    @Enumerated(EnumType.STRING)
    private Division division;

    private String descripcion;


}


