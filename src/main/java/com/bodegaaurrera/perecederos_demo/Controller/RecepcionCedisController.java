package com.bodegaaurrera.perecederos_demo.Controller;

import com.bodegaaurrera.perecederos_demo.Model.RecepcionCedis;
import com.bodegaaurrera.perecederos_demo.Service.RecepcionCedisService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recepciones/cedis")
public class RecepcionCedisController {

    private final RecepcionCedisService recepcionCedisService;

    public RecepcionCedisController(RecepcionCedisService recepcionCedisService) {
        this.recepcionCedisService = recepcionCedisService;
    }

    @PostMapping
    public RecepcionCedis registrarRecepcion(@RequestBody RecepcionCedis recepcion) {
        return recepcionCedisService.registrarRecepcion(recepcion);
    }

    @GetMapping("/camion/{numeroCamion}")
    public List<RecepcionCedis> obtenerPorCamion(@PathVariable String numeroCamion) {
        return recepcionCedisService.obtenerPorCamion(numeroCamion);
    }

    @GetMapping("/departamento/{departamento}")
    public List<RecepcionCedis> obtenerPorDepartamento(@PathVariable String departamento) {
        return recepcionCedisService.obtenerPorDepartamento(departamento);
    }

    @GetMapping("/division/{division}")
    public List<RecepcionCedis> obtenerPorDivision(@PathVariable String division) {
        return recepcionCedisService.obtenerPorDivision(division);
    }
}
