package com.bodegaaurrera.perecederos_demo.Controller;

import com.bodegaaurrera.perecederos_demo.DTO.ApiResponse;
import com.bodegaaurrera.perecederos_demo.DTO.AlertaInventario;
import com.bodegaaurrera.perecederos_demo.DTO.InventarioDTO;
import com.bodegaaurrera.perecederos_demo.DTO.InventarioDetalleDTO;
import com.bodegaaurrera.perecederos_demo.Service.InventarioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ApiResponse<List<InventarioDTO>> listarInventario() {
        return new ApiResponse<>(inventarioService.listarTodo());
    }

    @GetMapping("/caducados")
    public ApiResponse<List<InventarioDTO>> listarCaducados() {
        return new ApiResponse<>(inventarioService.listarCaducados());
    }

    @GetMapping("/por-caducar")
    public ApiResponse<List<InventarioDTO>> listarPorCaducar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate limite) {
        return new ApiResponse<>(inventarioService.listarPorCaducar(limite));
    }

    // ✅ Generar alertas automáticas
    @GetMapping("/alertas")
    public ApiResponse<List<AlertaInventario>> obtenerAlertas() {
        return new ApiResponse<>(inventarioService.generarAlertasCaducidad());
    }


    // ✅ Filtrar por división
    @GetMapping("/division")
    public ApiResponse<List<InventarioDTO>> obtenerPorDivision(@RequestParam String division) {
        return new ApiResponse<>(inventarioService.obtenerPorDivision(division));
    }

    // ✅ Filtrar por departamento
    @GetMapping("/departamento")
    public ApiResponse<List<InventarioDTO>> obtenerPorDepartamento(@RequestParam String departamento) {
        return new ApiResponse<>(inventarioService.obtenerPorDepartamento(departamento));
    }
    @GetMapping("/scan/{upc}")
    public ResponseEntity<InventarioDetalleDTO> escanear(@PathVariable String upc) {
        return ResponseEntity.ok(inventarioService.obtenerDetallePorUpc(upc));
    }

}