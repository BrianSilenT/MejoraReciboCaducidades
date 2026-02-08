package com.bodegaaurrera.perecederos_demo.Controller;

import com.bodegaaurrera.perecederos_demo.Model.EstadoOrden;
import com.bodegaaurrera.perecederos_demo.Model.EstadoOrdenRequest;
import com.bodegaaurrera.perecederos_demo.Model.OrdenCompra;
import com.bodegaaurrera.perecederos_demo.Service.OrdenCompraService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes-compra")
public class OrdenCompraController {

    private final OrdenCompraService ordenCompraService;

    public OrdenCompraController(OrdenCompraService ordenCompraService) {
        this.ordenCompraService = ordenCompraService;
    }

    // ✅ Listar todas las órdenes
    @GetMapping
    public List<OrdenCompra> listarOrdenes() {
        return ordenCompraService.listarOrdenes();
    }

    // ✅ Registrar nueva orden
    @PostMapping
    public OrdenCompra registrarOrden(@RequestBody OrdenCompra orden) {
        return ordenCompraService.registrarOrden(orden);
    }

    // ✅ Validar vigencia de una orden
    @GetMapping("/{id}/vigente")
    public String validarVigencia(@PathVariable("id") Long id) {
        boolean vigente = ordenCompraService.estaVigente(id);
        return vigente ? "La orden está vigente" : "La orden está expirada";
    }

    // ✅ Actualizar estado de una orden
    @PutMapping("/{id}/estado")
    public OrdenCompra actualizarEstado(@PathVariable Long id, @RequestBody EstadoOrdenRequest request) {
        EstadoOrden estado = EstadoOrden.valueOf(request.getNuevoEstado()); // conversión String → Enum
        return ordenCompraService.actualizarEstado(id, estado);
    }
}
