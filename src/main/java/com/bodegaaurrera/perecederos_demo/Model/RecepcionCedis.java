package com.bodegaaurrera.perecederos_demo.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "recepcion_cedis")
public class RecepcionCedis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRecepcionCedis;

    private String numeroCamion;
    private LocalDate fechaRecepcion;

    @Enumerated(EnumType.STRING)
    private Division division;

    @Enumerated(EnumType.STRING)
    private Departamento departamento;

    private int totalEsperado;
    private int totalRecibido;
    private double porcentajeAuditado;

    private boolean completa;
}
