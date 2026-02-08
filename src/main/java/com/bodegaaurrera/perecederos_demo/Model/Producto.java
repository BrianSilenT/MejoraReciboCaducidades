package com.bodegaaurrera.perecederos_demo.Model;

import jakarta.persistence.*;
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


    private String codigoBarras;
    private String Descripcion;
    private String nombre;
    private String categoria;
    private String presentacion;
    private String proveedor;
    private String Lote;



    // getters y setters
}


