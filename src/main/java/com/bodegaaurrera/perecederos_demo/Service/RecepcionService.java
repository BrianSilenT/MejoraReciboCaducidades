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

    public RecepcionService(RecepcionRepository recepcionRepository,
                            OrdenCompraRepository ordenCompraRepository) {
        this.recepcionRepository = recepcionRepository;
        this.ordenCompraRepository = ordenCompraRepository;
    }

    // 🔹 Listar todas las recepciones
    public List<Recepcion> listarRecepciones() {
        return recepcionRepository.findAll();
    }

    // 🔹 Listar recepciones por estado
    public List<Recepcion> listarPorEstado(String estado) {
        return recepcionRepository.findByEstado(EstadoRecepcion.valueOf(estado));
    }

    public List<Recepcion> listarCaducadas(LocalDate limite) {
        return recepcionRepository.findByFechaCaducidadBefore(limite);
    }

    // 🔹 Guardar recepción con validaciones
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

        long diasVida = ChronoUnit.DAYS.between(
                recepcion.getFechaRecepcion(),
                recepcion.getFechaCaducidad()
        );
        if (diasVida < 10) {
            throw new IllegalArgumentException("El producto tiene menos de 10 días de vida útil.");
        }

        recepcion.setEstado(EstadoRecepcion.ACEPTADA);
        recepcion.setOrdenCompra(orden);

        return recepcionRepository.save(recepcion);
    }

    LocalDate limite = LocalDate.now().plusDays(3);
    public List<Recepcion> listarPorCaducar(LocalDate limite) {
        return recepcionRepository.findByFechaCaducidadBefore(limite);
    }


}