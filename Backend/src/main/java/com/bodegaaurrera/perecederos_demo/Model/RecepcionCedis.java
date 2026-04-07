package com.bodegaaurrera.perecederos_demo.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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

    @Column(name = "cantidad_rpc")
    @Min(value = 0, message = "La cantidad de RPC no puede ser negativa")
    private Integer cantidadRpc; // ahora es objeto, puede ser null
}
