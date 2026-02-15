package com.bodegaaurrera.perecederos_demo.Controller;

import com.bodegaaurrera.perecederos_demo.Model.ApiResponse;
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
    @GetMapping("/list")
    public ApiResponse<List<OrdenCompra>> listarOrdenes() {
        List<OrdenCompra> ordenes = ordenCompraService.listarOrdenes();
        return new ApiResponse<>(ordenes);
    }

    // ✅ Registrar nueva orden
    @PostMapping
    public ApiResponse<OrdenCompra> registrarOrden(@RequestBody OrdenCompra orden) {
        OrdenCompra nueva = ordenCompraService.registrarOrden(orden);
        return new ApiResponse<>(nueva);
    }

    // ✅ Validar vigencia de una orden
    @GetMapping("/{id}/vigente")
    public ApiResponse<String> validarVigencia(@PathVariable("id") Long id) {
        boolean vigente = ordenCompraService.estaVigente(id);
        String mensaje = vigente ? "La orden está vigente" : "La orden está expirada";
        return new ApiResponse<>(mensaje);
    }

    // ✅ Actualizar estado de una orden
    @PutMapping("/{id}/estado")
    public ApiResponse<OrdenCompra> actualizarEstado(@PathVariable Long id, @RequestBody EstadoOrdenRequest request) {
        EstadoOrden estado = EstadoOrden.valueOf(request.getNuevoEstado().toUpperCase());
        OrdenCompra actualizada = ordenCompraService.actualizarEstado(id, estado);
        return new ApiResponse<>(actualizada);
    }
}
