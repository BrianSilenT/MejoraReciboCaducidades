package com.bodegaaurrera.perecederos_demo.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
@Getter
@Setter
@Entity
@Table(name = "rpc_control")
public class RpcControl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRpc;

    @NotBlank
    private String numeroCamion;

    @Enumerated(EnumType.STRING)
    private Departamento departamento; // Enum: FRUTAS, VERDURAS, etc.

    @Min(1)
    private int cantidadEntregada;

    @Min(0)
    private int cantidadRetornada;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaRegistro;

    private boolean pendienteRetorno;
}