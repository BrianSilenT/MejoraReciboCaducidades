package com.bodegaaurrera.perecederos_demo.Controller;

import com.bodegaaurrera.perecederos_demo.Model.ApiResponse;
import com.bodegaaurrera.perecederos_demo.Model.Recepcion;
import com.bodegaaurrera.perecederos_demo.Service.RecepcionService;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/recepcion")
public class RecepcionController {

    private final RecepcionService recepcionService;

    public RecepcionController(RecepcionService recepcionService) {
        this.recepcionService = recepcionService;
    }

    // ✅ Registrar recepción de proveedor
    @PostMapping
    public ApiResponse<Recepcion> registrarRecepcion(@RequestBody Recepcion recepcion) {
        Recepcion nueva = recepcionService.registrar(recepcion);
        return new ApiResponse<>(nueva);
    }

    // ✅ Listar todas las recepciones
    @GetMapping
    public ApiResponse<List<Recepcion>> listarRecepciones() {
        List<Recepcion> recepciones = recepcionService.listarTodas();
        return new ApiResponse<>(recepciones);
    }
}