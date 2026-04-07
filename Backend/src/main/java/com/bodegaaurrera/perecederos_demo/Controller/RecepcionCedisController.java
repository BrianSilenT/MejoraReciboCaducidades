    package com.bodegaaurrera.perecederos_demo.Controller;

    import com.bodegaaurrera.perecederos_demo.DTO.RecepcionAuditoriaDTO;
    import com.bodegaaurrera.perecederos_demo.DTO.RecepcionCedisRequestDTO;
    import com.bodegaaurrera.perecederos_demo.DTO.RecepcionCedisResponseDTO;
    import com.bodegaaurrera.perecederos_demo.Model.ApiResponse;
    import com.bodegaaurrera.perecederos_demo.Model.RecepcionCedis;
    import com.bodegaaurrera.perecederos_demo.Service.RecepcionCedisService;
    import jakarta.validation.Valid;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
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

        @GetMapping("/camion/{numeroCamion}/departamento/{departamento}")
        public ApiResponse<RecepcionCedisResponseDTO> obtenerPorDepartamento(
                @PathVariable String numeroCamion,
                @PathVariable String departamento) {
            RecepcionCedisResponseDTO dto = recepcionCedisService.obtenerPorCamionYDepartamento(numeroCamion, departamento);
            return new ApiResponse<>(dto);
        }

        @GetMapping("/camion/{numeroCamion}")
        public ResponseEntity<?> obtenerPorCamion(@PathVariable String numeroCamion) {
            // 1. Buscamos la lista de recepciones asociadas al camión
            List<RecepcionCedis> recepciones = recepcionCedisService.obtenerPorCamion(numeroCamion);

            // 2. Validamos si el camión existe
            if (recepciones == null || recepciones.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("mensaje", "No hay carga registrada para el camión: " + numeroCamion));
            }

            // 3. Generamos el DTO usando tu lógica de "Respuesta Completa"
            RecepcionCedisResponseDTO resultado = recepcionCedisService.construirRespuestaCompleta(recepciones);

            // 4. Respondemos con éxito
            return ResponseEntity.ok(new ApiResponse<>(resultado));
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

        @PostMapping
        public ResponseEntity<RecepcionAuditoriaDTO> registrarRecepcion(@Valid @RequestBody RecepcionCedisRequestDTO request) {
            RecepcionCedis recepcion = recepcionCedisService.registrarRecepcionConDetalles(request);
            return ResponseEntity.ok(recepcionCedisService.construirRespuestaPlano(recepcion));
        }

        @PutMapping("/{id}/cerrar")
        public ResponseEntity<RecepcionAuditoriaDTO> cerrarRecepcion(@PathVariable Long id) {
            RecepcionAuditoriaDTO auditoria = recepcionCedisService.cerrarRecepcion(id);
            return ResponseEntity.ok(auditoria);
        }

    }
