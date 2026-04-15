package com.bodegaaurrera.perecederos_demo.Controller;


import com.bodegaaurrera.perecederos_demo.Service.AuditoriaSurtidoService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaSurtidoController {

    private final AuditoriaSurtidoService service;

    public AuditoriaSurtidoController(AuditoriaSurtidoService service) {
        this.service = service;
    }

    @PostMapping("/{upc}")
    public void auditar(@PathVariable String upc) {
        service.auditar(upc);
    }
}