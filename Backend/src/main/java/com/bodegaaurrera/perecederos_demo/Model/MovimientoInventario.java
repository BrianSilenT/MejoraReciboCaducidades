package com.bodegaaurrera.perecederos_demo.Model;


import com.bodegaaurrera.perecederos_demo.Enums.TipoMovimiento;
import com.bodegaaurrera.perecederos_demo.Enums.Ubicacion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "movimiento_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private String lote;

    private int cantidad;

    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipoMovimiento;

    @Enumerated(EnumType.STRING)
    private Ubicacion ubicacionOrigen;

    @Enumerated(EnumType.STRING)
    private Ubicacion ubicacionDestino;

    private String referencia; // ejemplo: id recepción, ajuste, etc.

    private String usuario; // luego lo conectas con JWT

    private String motivo; // merma, ajuste, venta manual, etc.

    private LocalDateTime fecha;
}