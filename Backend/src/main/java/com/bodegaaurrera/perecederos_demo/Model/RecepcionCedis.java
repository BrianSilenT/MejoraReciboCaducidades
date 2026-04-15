package com.bodegaaurrera.perecederos_demo.Model;

import com.bodegaaurrera.perecederos_demo.Enums.Departamento;
import com.bodegaaurrera.perecederos_demo.Enums.Division;
import com.bodegaaurrera.perecederos_demo.Enums.EstadoRecepcion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "recepcion_cedis")
public class RecepcionCedis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id_recepcion_cedis")
    private Long idRecepcion;

    private String numeroCamion;
    private LocalDate fechaRecepcion;

    @Enumerated(EnumType.STRING)
    private EstadoRecepcion estado;

    @Enumerated(EnumType.STRING)
    private Division division;

    @Enumerated(EnumType.STRING)
    private Departamento departamento;

    private int totalEsperado;
    private int totalRecibido;
    private Double porcentajeAuditado;

    private boolean completa;

    @OneToMany(mappedBy = "recepcionCedis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecepcionCedisDetalle> detalles;

}
