package com.bodegaaurrera.perecederos_demo.Controller;

import com.bodegaaurrera.perecederos_demo.DTO.RecepcionAuditoriaDTO;
import com.bodegaaurrera.perecederos_demo.DTO.RecepcionCedisRequestDTO;
import com.bodegaaurrera.perecederos_demo.DTO.RecepcionCedisResponseDTO;
import com.bodegaaurrera.perecederos_demo.Model.ApiResponse;
import com.bodegaaurrera.perecederos_demo.Model.RecepcionCedis;
import com.bodegaaurrera.perecederos_demo.Service.RecepcionCedisService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recepciones/cedis")
public class RecepcionCedisController {

    @Autowired
    private final RecepcionCedisService recepcionCedisService;

    public RecepcionCedisController(RecepcionCedisService recepcionCedisService) {
        this.recepcionCedisService = recepcionCedisService;
    }

    @GetMapping("/departamento/{departamento}")
    public ApiResponse<RecepcionCedisResponseDTO> obtenerPorDepartamento(@PathVariable String departamento) {
        RecepcionCedisResponseDTO dto = recepcionCedisService.obtenerPorDepartamentoConDTO(departamento);
        return new ApiResponse<>(dto);
    }


    @GetMapping("/division/{division}")
    public ApiResponse<List<RecepcionCedis>> obtenerPorDivision(@PathVariable String division) {
        return new ApiResponse<>(recepcionCedisService.obtenerPorDivision(division));
    }

    @GetMapping("/camion/{numeroCamion}")
    public ApiResponse<RecepcionCedisResponseDTO> obtenerPorCamion(@PathVariable String numeroCamion) {
        List<RecepcionCedis> recepciones = recepcionCedisService.obtenerPorCamion(numeroCamion);
        if (recepciones.isEmpty()) {
            return new ApiResponse<>(new RecepcionCedisResponseDTO());
        }
        RecepcionCedisResponseDTO dto = recepcionCedisService.construirRespuestaCompleta(recepciones);
        return new ApiResponse<>(dto);
    }

    @PostMapping
    public ResponseEntity<RecepcionAuditoriaDTO> registrarRecepcion(@Valid @RequestBody RecepcionCedisRequestDTO request) {
        RecepcionCedis recepcion = recepcionCedisService.registrarRecepcionConDetalles(request);
        return ResponseEntity.ok(recepcionCedisService.construirRespuestaPlano(recepcion));
    }

    @GetMapping("/camiones")
    public ResponseEntity<?> listarCamiones() {
        List<Map<String, Object>> camiones = recepcionCedisService.listarCamiones();
        return ResponseEntity.ok(Map.of(
                "data", camiones,
                "total", camiones.size(),
                "fecha", LocalDateTime.now()
        ));
    }


    @GetMapping("/departamento/frutas-verduras/{numeroCamion}")
    public ApiResponse<RecepcionCedisResponseDTO> obtenerFrutasYVerduras(@PathVariable String numeroCamion) {
        return new ApiResponse<>(recepcionCedisService.obtenerFrutasYVerduras(numeroCamion));
    }

    @PutMapping("/{id}/cerrar")
    public ResponseEntity<RecepcionAuditoriaDTO> cerrarRecepcion(@PathVariable Long id) {
        RecepcionAuditoriaDTO auditoria = recepcionCedisService.cerrarRecepcion(id);
        return ResponseEntity.ok(auditoria);
    }

}
