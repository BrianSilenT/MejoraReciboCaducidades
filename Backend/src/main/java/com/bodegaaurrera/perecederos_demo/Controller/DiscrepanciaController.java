package com.bodegaaurrera.perecederos_demo.Controller;

import com.bodegaaurrera.perecederos_demo.Model.ApiResponse;
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

    @PostMapping
    public ApiResponse<DiscrepanciaRecepcion> registrarDiscrepancia(@RequestBody DiscrepanciaRecepcion discrepancia) {
        DiscrepanciaRecepcion nueva = discrepanciaService.registrarDiscrepancia(discrepancia);
        return new ApiResponse<>(nueva);
    }

    @GetMapping
    public ApiResponse<List<DiscrepanciaRecepcion>> listarDiscrepancias() {
        List<DiscrepanciaRecepcion> discrepancias = discrepanciaService.listarTodas();
        return new ApiResponse<>(discrepancias);
    }
}
