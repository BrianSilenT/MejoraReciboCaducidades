package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.Model.EstadoRecepcion;
import com.bodegaaurrera.perecederos_demo.Model.Recepcion;
import com.bodegaaurrera.perecederos_demo.Model.RecepcionDetalle;
import com.bodegaaurrera.perecederos_demo.Repository.RecepcionRepository;
import com.bodegaaurrera.perecederos_demo.Repository.OrdenCompraRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RecepcionService {

    private final RecepcionRepository recepcionRepository;
    private final OrdenCompraRepository ordenCompraRepository;
    private final InventarioService inventarioService;

    public RecepcionService(RecepcionRepository recepcionRepository,
                            OrdenCompraRepository ordenCompraRepository,
                            InventarioService inventarioService) {
        this.recepcionRepository = recepcionRepository;
        this.ordenCompraRepository = ordenCompraRepository;
        this.inventarioService = inventarioService;
    }

    // ✅ nombre consistente con el controller
    public List<Recepcion> listarTodas() {
        return recepcionRepository.findAll();
    }

    public List<Recepcion> listarPorEstado(String estado) {
        return recepcionRepository.findByEstado(EstadoRecepcion.valueOf(estado.toUpperCase()));
    }

    // ✅ nombre consistente con el controller
    public Recepcion registrar(Recepcion recepcion) {
        Long idOrden = recepcion.getOrdenCompra().getIdOrden();
        var orden = ordenCompraRepository.findById(idOrden)
                .orElseThrow(() -> new IllegalArgumentException("Orden de compra no encontrada"));

        LocalDate limite = orden.getFechaEmision().plusDays(orden.getVigenciaDias());
        if (LocalDate.now().isAfter(limite)) {
            throw new IllegalArgumentException("La orden de compra está expirada.");
        }

        if (recepcion.getFechaRecepcion() == null) {
            recepcion.setFechaRecepcion(LocalDate.now());
        }

        for (RecepcionDetalle detalle : recepcion.getProductos()) {
            var productoOrden = orden.getProductos().stream()
                    .filter(po -> po.getProducto().getCodigoBarras().equals(detalle.getProducto().getCodigoBarras()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Producto no pertenece a la orden"));

            if (detalle.getCantidadRecibida() > productoOrden.getCantidadEsperada()) {
                throw new IllegalArgumentException("Cantidad recibida excede la solicitada en la O.C.");
            }

            if (detalle.getFechaCaducidad() == null) {
                throw new IllegalArgumentException("La recepción debe incluir fecha de caducidad válida.");
            }

            long diasVida = ChronoUnit.DAYS.between(
                    recepcion.getFechaRecepcion(),
                    detalle.getFechaCaducidad()
            );

            if (diasVida < 10) {
                throw new IllegalArgumentException("El producto tiene menos de 10 días de vida útil.");
            }

            // 🔹 Integrar descripción desde la orden/producto
            detalle.getProducto().setDescripcion(productoOrden.getProducto().getDescripcion());

            // Cargar en inventario con descripción
            inventarioService.cargarInventarioDesdeRecepcion(detalle);

            detalle.setRecepcion(recepcion);
        }

        recepcion.setEstado(EstadoRecepcion.ACEPTADA);
        recepcion.setOrdenCompra(orden);

        return recepcionRepository.save(recepcion);
    }
}