package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.Enums.EstadoOrden;
import com.bodegaaurrera.perecederos_demo.Enums.EstadoRecepcion;
import com.bodegaaurrera.perecederos_demo.Enums.TipoMovimiento;
import com.bodegaaurrera.perecederos_demo.Enums.Ubicacion;
import com.bodegaaurrera.perecederos_demo.Model.*;
import com.bodegaaurrera.perecederos_demo.Repository.OrdenCompraRepository;
import com.bodegaaurrera.perecederos_demo.Repository.RecepcionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RecepcionService {

    private final RecepcionRepository recepcionRepository;
    private final OrdenCompraRepository ordenCompraRepository;
    private final MovimientoInventarioService movimientoService;

    // ============================
    // REGISTRAR (NO impacta inventario)
    // ============================
    @Transactional
    public Recepcion registrar(Recepcion recepcion) {

        OrdenCompra orden = validarOrdenVigente(recepcion.getOrdenCompra().getIdOrden());

        recepcion.setOrdenCompra(orden);
        recepcion.setFechaRecepcion(LocalDate.now());
        recepcion.setEstado(EstadoRecepcion.BORRADOR);

        for (RecepcionDetalle detalle : recepcion.getDetalles()) {

            if (detalle.getOrdenCompraDetalle() == null) {
                throw new IllegalArgumentException("Detalle sin relación a orden de compra");
            }

            if (!detalle.getOrdenCompraDetalle().getOrdenCompra().getIdOrden()
                    .equals(orden.getIdOrden())) {
                throw new IllegalArgumentException("El detalle no pertenece a la orden");
            }

            validarDetalle(detalle, recepcion.getFechaRecepcion());
            detalle.setRecepcion(recepcion);
        }

        return recepcionRepository.save(recepcion);
    }

    // ============================
    // CONFIRMAR (impacta inventario)
    // ============================
    @Transactional
    public void confirmar(Long idRecepcion) {

        Recepcion recepcion = recepcionRepository.findById(idRecepcion)
                .orElseThrow(() -> new IllegalArgumentException("Recepción no encontrada"));

        if (recepcion.getEstado() == EstadoRecepcion.CONFIRMADA) {
            throw new IllegalArgumentException("La recepción ya fue confirmada");
        }

        OrdenCompra orden = recepcion.getOrdenCompra();

        if (orden.getEstado() != EstadoOrden.VIGENTE &&
                orden.getEstado() != EstadoOrden.PARCIAL) {
            throw new IllegalArgumentException("La orden no está disponible para recepción");
        }

        for (RecepcionDetalle detalle : recepcion.getDetalles()) {

            OrdenCompraDetalle ocDetalle = detalle.getOrdenCompraDetalle();

            if (ocDetalle == null) {
                throw new IllegalArgumentException("Detalle sin referencia a orden de compra");
            }

            validarCantidad(detalle, ocDetalle);

            // 🔥 INVENTARIO
            movimientoService.ejecutarMovimiento(
                    detalle.getProducto().getCodigoBarras(),
                    detalle.getLote(),
                    detalle.getCantidadRecibida(),
                    TipoMovimiento.RECEPCION,
                    null,
                    Ubicacion.BODEGA,
                    "OC-" + orden.getIdOrden(),
                    "Entrada proveedor"
            );

            // 🔥 SUMAR RECIBIDO (null safe)
            BigDecimal actual = ocDetalle.getCantidadRecibida() != null
                    ? ocDetalle.getCantidadRecibida()
                    : BigDecimal.ZERO;

            ocDetalle.setCantidadRecibida(
                    actual.add(detalle.getCantidadRecibida())
            );
        }

        recepcion.setEstado(EstadoRecepcion.CONFIRMADA);

        actualizarEstadoOrden(orden);

        ordenCompraRepository.save(orden);
        recepcionRepository.save(recepcion);
    }

    // ============================
    // VALIDACIONES
    // ============================
    private OrdenCompra validarOrdenVigente(Long idOrden) {
        OrdenCompra orden = ordenCompraRepository.findById(idOrden)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));

        LocalDate limite = orden.getFechaEmision().plusDays(orden.getVigenciaDias());

        if (LocalDate.now().isAfter(limite) || orden.getEstado() != EstadoOrden.VIGENTE) {
            throw new IllegalArgumentException("Orden no vigente");
        }

        return orden;
    }

    private void validarDetalle(RecepcionDetalle detalle, LocalDate fechaRef) {

        if (detalle.getFechaCaducidad() == null) {
            throw new IllegalArgumentException("Fecha de caducidad obligatoria");
        }

        long diasVida = ChronoUnit.DAYS.between(fechaRef, detalle.getFechaCaducidad());

        if (diasVida < 10) {
            throw new IllegalArgumentException("Vida útil menor a 10 días");
        }

        if (detalle.getCantidadRecibida() == null ||
                detalle.getCantidadRecibida().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }
    }

    private void validarCantidad(RecepcionDetalle detalle, OrdenCompraDetalle ocDetalle) {

        BigDecimal actual = ocDetalle.getCantidadRecibida() != null
                ? ocDetalle.getCantidadRecibida()
                : BigDecimal.ZERO;

        BigDecimal total = actual.add(detalle.getCantidadRecibida());

        if (total.compareTo(ocDetalle.getCantidadEsperada()) > 0) {
            throw new IllegalArgumentException("Excede cantidad solicitada en la orden");
        }
    }

    // ============================
    // ESTADO DE ORDEN
    // ============================
    private void actualizarEstadoOrden(OrdenCompra orden) {

        boolean completa = true;
        boolean parcial = false;

        for (OrdenCompraDetalle det : orden.getDetalles()) {

            BigDecimal recibida = det.getCantidadRecibida() != null
                    ? det.getCantidadRecibida()
                    : BigDecimal.ZERO;

            BigDecimal esperada = det.getCantidadEsperada();

            if (recibida.compareTo(BigDecimal.ZERO) == 0) {
                completa = false;
            } else if (recibida.compareTo(esperada) < 0) {
                completa = false;
                parcial = true;
            }
        }

        if (completa) {
            orden.setEstado(EstadoOrden.COMPLETADA);
        } else if (parcial) {
            orden.setEstado(EstadoOrden.PARCIAL);
        } else {
            orden.setEstado(EstadoOrden.VIGENTE);
        }
    }

    // ============================
    // CONSULTAS
    // ============================
    public List<Recepcion> listarTodas() {
        return recepcionRepository.findAll();
    }
}