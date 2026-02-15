package com.bodegaaurrera.perecederos_demo.Controller;

import com.bodegaaurrera.perecederos_demo.Model.ApiResponse;
import com.bodegaaurrera.perecederos_demo.Model.AlertaInventario;
import com.bodegaaurrera.perecederos_demo.Model.Inventario;
import com.bodegaaurrera.perecederos_demo.Service.InventarioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    // ✅ Listar todo el inventario
    @GetMapping
    public ApiResponse<List<Inventario>> listarInventario() {
        return new ApiResponse<>(inventarioService.listarTodo());
    }

    // ✅ Listar productos caducados
    @GetMapping("/caducados")
    public ApiResponse<List<Inventario>> listarCaducados(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate limite) {
        return new ApiResponse<>(inventarioService.listarPorCaducar(limite));
    }

    // ✅ Listar productos próximos a caducar
    @GetMapping("/por-caducar")
    public ApiResponse<List<Inventario>> listarPorCaducar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate limite) {
        return new ApiResponse<>(inventarioService.listarPorCaducar(limite));
    }

    // ✅ Generar alertas automáticas
    @GetMapping("/alertas")
    public ApiResponse<List<AlertaInventario>> obtenerAlertas() {
        return new ApiResponse<>(inventarioService.generarAlertasCaducidad());
    }

    // ✅ Buscar inventario por código de barras
    @GetMapping("/buscar")
    public ApiResponse<Inventario> obtenerPorCodigoBarras(@RequestParam String codigoBarras) {
        return new ApiResponse<>(inventarioService.obtenerPorCodigoBarras(codigoBarras));
    }

    // ✅ Filtrar por división
    @GetMapping("/division")
    public ApiResponse<List<Inventario>> obtenerPorDivision(@RequestParam String division) {
        return new ApiResponse<>(inventarioService.obtenerPorDivision(division));
    }

    // ✅ Filtrar por departamento
    @GetMapping("/departamento")
    public ApiResponse<List<Inventario>> obtenerPorDepartamento(@RequestParam String departamento) {
        return new ApiResponse<>(inventarioService.obtenerPorDepartamento(departamento));
    }
}