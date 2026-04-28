package com.bodegaaurrera.perecederos_demo.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table( name ="producto_alias")
@Getter
@Setter
public class ProductoAlias {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigoVenta; // upc de salida
    private String codigoInventario; // upc real recepcion

    private boolean activo = true;
}
