package com.bodegaaurrera.perecederos_demo.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_surtido")
@Getter
@Setter
public class AuditoriaSurtido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String upc;
    private String lote;

    private BigDecimal cantidadSugerida;
    private BigDecimal cantidadSurtida;

    private boolean correcto; // si respetó FEFO

    private String motivo; // explicación del error

    private LocalDateTime fecha;
}
