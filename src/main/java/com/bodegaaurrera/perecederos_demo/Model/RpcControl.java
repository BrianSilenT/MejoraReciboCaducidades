package com.bodegaaurrera.perecederos_demo.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "rpc_control")
public class RpcControl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRpc;

    private String numeroCamion;
    private String departamento; // Frutas y Verduras
    private int cantidadEntregada;
    private int cantidadRetornada;
    private LocalDate fechaRegistro;

    private boolean pendienteRetorno;
}