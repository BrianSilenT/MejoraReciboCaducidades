package com.bodegaaurrera.perecederos_demo.Controller;

import com.bodegaaurrera.perecederos_demo.Model.AlertaInventario;
import com.bodegaaurrera.perecederos_demo.Model.Inventario;
import com.bodegaaurrera.perecederos_demo.Service.InventarioService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    // 🔹 Consultar inventario por código de barras
    @GetMapping("/{codigoBarras}")
    public Inventario consultar(@PathVariable("codigoBarras") String codigoBarras) {
        return inventarioService.consultarInventario(codigoBarras);
    }

    // 🔹 Listar inventario por división
    @GetMapping("/division/{division}")
    public List<Inventario> listarPorDivision(@PathVariable("division") String division) {
        return inventarioService.obtenerPorDivision(division);
    }

    // 🔹 Listar inventario por departamento
    @GetMapping("/departamento/{departamento}")
    public List<Inventario> listarPorDepartamento(@PathVariable("departamento") String departamento) {
        return inventarioService.obtenerPorDepartamento(departamento);
    }

    // 🔹 Listar productos próximos a caducar (ej. dentro de X días)
    @GetMapping("/por-caducar/{dias}")
    public List<Inventario> listarPorCaducar(@PathVariable int dias) {
        LocalDate limite = LocalDate.now().plusDays(dias);
        return inventarioService.listarPorCaducar(limite);
    }

    @GetMapping("/alertas")
    public List<AlertaInventario> obtenerAlertasCaducidad() {
        return inventarioService.generarAlertasCaducidad();
    }

    // 🔹 Nuevo: Alertas automáticas para piso de ventas
    //@GetMapping("/alertas")
    //public List<Map<String, Object>> obtenerAlertasCaducidad() {
      //  return inventarioService.generarAlertasCaducidad();
    //}

    // 🔹 Opcional: Tabla de control ordenada por fecha de caducidad
    @GetMapping("/control-caducidad")
    public List<Inventario> listarPorFechaCaducidad() {
        return inventarioService.listarPorCaducar(LocalDate.now().plusYears(1))
                .stream()
                .sorted(java.util.Comparator.comparing(Inventario::getFechaCaducidad))
                .toList();
    }
}