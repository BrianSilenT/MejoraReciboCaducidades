package com.bodegaaurrera.perecederos_demo.Controller;

import com.bodegaaurrera.perecederos_demo.DTO.EtiquetaRequestDTO;
import com.bodegaaurrera.perecederos_demo.Service.EtiquetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/etiquetas")
@RequiredArgsConstructor
public class EtiquetaController {

    private final EtiquetaService etiquetaService;

    @PostMapping("/peps")
    public ResponseEntity<List<String>> generar(@RequestBody EtiquetaRequestDTO request) {

        return ResponseEntity.ok(
                etiquetaService.generarEtiquetas(request)
        );
    }
}
