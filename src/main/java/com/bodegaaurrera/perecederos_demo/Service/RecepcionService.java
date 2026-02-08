package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.Model.EstadoRecepcion;
import com.bodegaaurrera.perecederos_demo.Model.Recepcion;
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
    private final InventarioService inventarioService; // 🔹 nuevo

    public RecepcionService(RecepcionRepository recepcionRepository,
                            OrdenCompraRepository ordenCompraRepository,
                            InventarioService inventarioService) {
        this.recepcionRepository = recepcionRepository;
        this.ordenCompraRepository = ordenCompraRepository;
        this.inventarioService = inventarioService;
    }

    public List<Recepcion> listarRecepciones() {
        return recepcionRepository.findAll();
    }

    public List<Recepcion> listarPorEstado(String estado) {
        return recepcionRepository.findByEstado(EstadoRecepcion.valueOf(estado));
    }

    public List<Recepcion> listarCaducadas(LocalDate limite) {
        return recepcionRepository.findByFechaCaducidadBefore(limite);
    }

    public List<Recepcion> listarPorCaducar(LocalDate limite) {
        return recepcionRepository.findByFechaCaducidadBefore(limite);
    }

    public Recepcion guardarRecepcion(Recepcion recepcion) {
        Long idOrden = recepcion.getOrdenCompra().getIdOrden();
        var orden = ordenCompraRepository.findById(idOrden)
                .orElseThrow(() -> new IllegalArgumentException("Orden de compra no encontrada"));

        LocalDate limite = orden.getFechaEmision().plusDays(orden.getVigenciaDias());
        if (LocalDate.now().isAfter(limite)) {
            throw new IllegalArgumentException("La orden de compra está expirada.");
        }

        if (recepcion.getCantidad() > orden.getCantidadSolicitada()) {
            throw new IllegalArgumentException("La cantidad recibida excede la solicitada en la O.C.");
        }

        if (recepcion.getFechaCaducidad() == null) {
            throw new IllegalArgumentException("La recepción debe incluir fecha de caducidad válida.");
        }

        if (recepcion.getFechaRecepcion() == null) {
            recepcion.setFechaRecepcion(LocalDate.now()); // opcional: setear fecha actual
        }

        long diasVida = ChronoUnit.DAYS.between(
                recepcion.getFechaRecepcion(),
                recepcion.getFechaCaducidad());

        if (diasVida < 10) {
            throw new IllegalArgumentException("El producto tiene menos de 10 días de vida útil.");
        }

        // 🔹 Validar duplicados de lote con misma fecha de caducidad
        boolean existeDuplicado = recepcionRepository
                .findByProductoAndLoteAndFechaCaducidad(
                        recepcion.getProducto(),
                        recepcion.getLote(),
                        recepcion.getFechaCaducidad()
                ).isPresent();

        if (existeDuplicado) {
            throw new IllegalArgumentException(
                    "Ya existe una recepción para este lote con la misma fecha de caducidad."
            );
        }

        recepcion.setEstado(EstadoRecepcion.ACEPTADA);
        recepcion.setOrdenCompra(orden);

        Recepcion guardada = recepcionRepository.save(recepcion);

        // 🔹 Integración automática al inventario
        inventarioService.cargarInventarioDesdeRecepcion(guardada);

        return guardada;
    }
}