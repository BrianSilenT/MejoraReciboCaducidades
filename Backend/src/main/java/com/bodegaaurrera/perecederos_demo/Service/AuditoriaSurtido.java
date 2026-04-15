package com.bodegaaurrera.perecederos_demo.Service;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    private int cantidadSugerida;
    private int cantidadSurtida;

    private boolean correcto; // si respetó FEFO

    private String motivo; // explicación del error

    private LocalDateTime fecha;
}
