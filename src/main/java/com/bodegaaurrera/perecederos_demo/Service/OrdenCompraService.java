package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.Model.EstadoOrden;
import com.bodegaaurrera.perecederos_demo.Model.OrdenCompra;
import com.bodegaaurrera.perecederos_demo.Repository.OrdenCompraRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrdenCompraService {

    private final OrdenCompraRepository ordenCompraRepository;

    public OrdenCompraService(OrdenCompraRepository ordenCompraRepository) {
        this.ordenCompraRepository = ordenCompraRepository;
    }

    public List<OrdenCompra> listarOrdenes() {
        return ordenCompraRepository.findAll();
    }

    // Guardar orden (alias registrarOrden)
    public OrdenCompra guardarOrden(OrdenCompra orden) {
        return registrarOrden(orden);
    }

    // Registrar nueva orden
    public OrdenCompra registrarOrden(OrdenCompra orden) {
        if (orden.getFechaEmision() == null) {
            orden.setFechaEmision(LocalDate.now());
        }
        orden.setEstado(EstadoOrden.VIGENTE);
        return ordenCompraRepository.save(orden);
    }

    // Buscar orden por ID
    public OrdenCompra buscarPorId(Long id) {
        return ordenCompraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orden de compra no encontrada"));
    }

    // Validar vigencia de una orden
    public boolean estaVigente(Long idOrden) {
        OrdenCompra orden = buscarPorId(idOrden);
        LocalDate limite = orden.getFechaEmision().plusDays(orden.getVigenciaDias());
        return !LocalDate.now().isAfter(limite) && orden.getEstado() == EstadoOrden.VIGENTE;
    }

    // Actualizar estado manualmente
    public OrdenCompra actualizarEstado(Long id, EstadoOrden nuevoEstado) {
        OrdenCompra orden = buscarPorId(id);
        orden.setEstado(nuevoEstado);
        return ordenCompraRepository.save(orden);
    }
}
