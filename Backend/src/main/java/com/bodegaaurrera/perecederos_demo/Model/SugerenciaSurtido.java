package com.bodegaaurrera.perecederos_demo.Model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sugerencia_surtido")
@Getter
@Setter
public class SugerenciaSurtido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String upc;

    private String lote;

    private int cantidadSugerida;

    private LocalDate fechaCaducidad;

    private LocalDateTime fechaGeneracion;
}
