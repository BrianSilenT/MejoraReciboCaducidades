package com.bodegaaurrera.perecederos_demo.Controller;

import com.bodegaaurrera.perecederos_demo.DTO.AuditoriaSurtidoDTO;
import com.bodegaaurrera.perecederos_demo.Service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping("/surtido")
    public List<AuditoriaSurtidoDTO> auditarSurtido() {
        return auditoriaService.auditarSurtido();
    }
}