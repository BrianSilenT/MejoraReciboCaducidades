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
            // válida que pertenezca a la orden
            buscarDetalleOrden(orden, detalle);

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

        for (RecepcionDetalle detalle : recepcion.getProductos()) {

            OrdenCompraDetalle ocDetalle = buscarDetalleOrden(orden, detalle);

            validarCantidad(detalle, ocDetalle);

            // 🔥 Actualizar inventario
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

            // 🔥 Actualizar cantidad recibida en OC
            ocDetalle.setCantidadRecibida(
                    ocDetalle.getCantidadRecibida() + detalle.getCantidadRecibida()
            );
        }

        recepcion.setEstado(EstadoRecepcion.CONFIRMADA);

        actualizarEstadoOrden(orden);

        // 🔥 persistir cambios
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

        if (detalle.getCantidadRecibida() <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }
    }

    private void validarCantidad(RecepcionDetalle detalle, OrdenCompraDetalle ocDetalle) {

        if (detalle.getCantidadRecibida() + ocDetalle.getCantidadRecibida() > ocDetalle.getCantidadEsperada()) {
            throw new IllegalArgumentException("Excede cantidad solicitada en la orden");
        }
    }

    private OrdenCompraDetalle buscarDetalleOrden(OrdenCompra orden, RecepcionDetalle detalle) {
        return orden.getDetalles().stream()
                .filter(d -> d.getProducto().getIdProducto()
                        .equals(detalle.getProducto().getIdProducto()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Producto no pertenece a la orden"));
    }

    // ============================
    // ESTADO DE ORDEN
    // ============================

    private void actualizarEstadoOrden(OrdenCompra orden) {

        boolean completa = true;
        boolean parcial = false;

        for (OrdenCompraDetalle det : orden.getDetalles()) {

            if (det.getCantidadRecibida() == 0) {
                completa = false;
            } else if (det.getCantidadRecibida() < det.getCantidadEsperada()) {
                completa = false;
                parcial = true;
            }
        }

        if (completa) {
            orden.setEstado(EstadoOrden.COMPLETADA);
        } else if (parcial) {
            orden.setEstado(EstadoOrden.PARCIAL);
        }
    }

    // ============================
    // CONSULTAS
    // ============================

    public List<Recepcion> listarTodas() {
        return recepcionRepository.findAll();
    }
}
