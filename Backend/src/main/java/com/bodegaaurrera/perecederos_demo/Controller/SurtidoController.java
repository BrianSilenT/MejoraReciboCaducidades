package com.bodegaaurrera.perecederos_demo.Controller;


import com.bodegaaurrera.perecederos_demo.DTO.MovimientoInventarioDTO;
import com.bodegaaurrera.perecederos_demo.DTO.SugerenciaSurtidoDTO;
import com.bodegaaurrera.perecederos_demo.Service.SurtidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/surtido")
public class SurtidoController {

    private final SurtidoService surtidoService;

    @GetMapping("/surtido/{upc}/{cantidad}")
    public ResponseEntity<SugerenciaSurtidoDTO> sugerir(
            @PathVariable String upc,
            @PathVariable int cantidad) {

        return ResponseEntity.ok(
                surtidoService.sugerirSurtido(upc, cantidad)
        );
    }

    @PostMapping("/ejecutar")
    public ResponseEntity<String> ejecutar(@RequestBody MovimientoInventarioDTO request) {

        surtidoService.ejecutarSurtido(request);

        return ResponseEntity.ok("Surtido ejecutado correctamente");
    }
}
