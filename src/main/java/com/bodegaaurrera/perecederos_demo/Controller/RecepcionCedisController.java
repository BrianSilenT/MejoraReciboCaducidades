package com.bodegaaurrera.perecederos_demo.Controller;

import com.bodegaaurrera.perecederos_demo.Model.ApiResponse;
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
    public ApiResponse<RecepcionCedis> registrarRecepcion(@RequestBody RecepcionCedis recepcion) {
        RecepcionCedis nueva = recepcionCedisService.registrarRecepcion(recepcion);
        return new ApiResponse<>(nueva);
    }

    @GetMapping("/camion/{numeroCamion}")
    public ApiResponse<List<RecepcionCedis>> obtenerPorCamion(@PathVariable String numeroCamion) {
        List<RecepcionCedis> recepciones = recepcionCedisService.obtenerPorCamion(numeroCamion);
        return new ApiResponse<>(recepciones);
    }

    @GetMapping("/departamento/{departamento}")
    public ApiResponse<List<RecepcionCedis>> obtenerPorDepartamento(@PathVariable String departamento) {
        List<RecepcionCedis> recepciones = recepcionCedisService.obtenerPorDepartamento(departamento);
        return new ApiResponse<>(recepciones);
    }

    @GetMapping("/division/{division}")
    public ApiResponse<List<RecepcionCedis>> obtenerPorDivision(@PathVariable String division) {
        List<RecepcionCedis> recepciones = recepcionCedisService.obtenerPorDivision(division);
        return new ApiResponse<>(recepciones);
    }
}
