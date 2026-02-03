package com.bodegaaurrera.perecederos_demo.Controller;

import com.bodegaaurrera.perecederos_demo.Model.Recepcion;
import com.bodegaaurrera.perecederos_demo.Service.RecepcionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/recepciones")
public class RecepcionController {

    private final RecepcionService recepcionService;

    public RecepcionController(RecepcionService recepcionService) {
        this.recepcionService = recepcionService;
    }

    @GetMapping
    public List<Recepcion> listarRecepciones() {
        return recepcionService.listarRecepciones();
    }

    @GetMapping("/caducadas/{fecha}")
    public List<Recepcion> listarCaducadas(@PathVariable String fecha) {
        LocalDate limite = LocalDate.parse(fecha);
        return recepcionService.listarCaducadas(limite);
    }

    @PostMapping
    public ResponseEntity<?> registrarRecepcion(@RequestBody Recepcion recepcion) {
        try {
            Recepcion guardada = recepcionService.guardarRecepcion(recepcion);
            return ResponseEntity.ok(guardada);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/rechazadas")
    public List<Recepcion> listarRechazadas() {
        return recepcionService.listarPorEstado("RECHAZADA");
    }

    @GetMapping("/aceptadas")
    public List<Recepcion> listarAceptadas() {
        return recepcionService.listarPorEstado("ACEPTADA");
    }

    @GetMapping("/por-caducar/{dias}")
    public List<Recepcion> listarPorCaducar(@PathVariable int dias) {
        LocalDate limite = LocalDate.now().plusDays(dias);
        return recepcionService.listarPorCaducar(limite);
    }
}