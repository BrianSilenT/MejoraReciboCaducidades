package com.bodegaaurrera.perecederos_demo.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "discrepancia_recepcion")
public class DiscrepanciaRecepcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDiscrepancia;

    private String numeroCamion;
    private String departamento;
    private BigDecimal totalEsperado;
    private BigDecimal totalRecibido;
    private BigDecimal totalFaltante;

    private LocalDate fechaRegistro;
}
