package com.bodegaaurrera.perecederos_demo.Controller;


import com.bodegaaurrera.perecederos_demo.DTO.AuditoriaSurtidoDTO;
import com.bodegaaurrera.perecederos_demo.Service.AuditoriaService;
import com.bodegaaurrera.perecederos_demo.Service.AuditoriaSurtidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaSurtidoController {

    private final AuditoriaSurtidoService service;
    @PostMapping("/{upc}")
    public void auditar(@PathVariable String upc) {
        service.auditar(upc);
    }

    @RestController
    @RequestMapping("/api/auditoria")
    public static class AuditoriaController {

        private final AuditoriaService auditoriaService;

        public AuditoriaController(AuditoriaService auditoriaService) {
            this.auditoriaService = auditoriaService;
        }

        @GetMapping("/surtido")
        public List<AuditoriaSurtidoDTO> auditar() {
            return auditoriaService.auditarSurtido();
        }
    }
}