package com.bodegaaurrera.perecederos_demo.Controller;

import com.bodegaaurrera.perecederos_demo.Model.DiscrepanciaRecepcion;
import com.bodegaaurrera.perecederos_demo.Service.DiscrepanciaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discrepancias")
public class DiscrepanciaController {

    private final DiscrepanciaService discrepanciaService;

    public DiscrepanciaController(DiscrepanciaService discrepanciaService) {
        this.discrepanciaService = discrepanciaService;
    }

    @GetMapping
    public List<DiscrepanciaRecepcion> obtenerTodas() {
        return discrepanciaService.obtenerTodas();
    }

    @GetMapping("/camion/{numeroCamion}")
    public List<DiscrepanciaRecepcion> obtenerPorCamion(@PathVariable String numeroCamion) {
        return discrepanciaService.obtenerPorCamion(numeroCamion);
    }

    @GetMapping("/departamento/{departamento}")
    public List<DiscrepanciaRecepcion> obtenerPorDepartamento(@PathVariable String departamento) {
        return discrepanciaService.obtenerPorDepartamento(departamento);
    }
}
