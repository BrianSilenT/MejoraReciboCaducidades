package com.bodegaaurrera.perecederos_demo.Sheduler;

import com.bodegaaurrera.perecederos_demo.Service.InventarioService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class AlertaCaducidadSheduler {

    private final InventarioService inventarioService;

    public AlertaCaducidadSheduler(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }
    
    @Scheduled(cron = "0 0 8 * * ?") // todos los días a las 8 AM
    public void revisarPorCaducar() {
        LocalDate limite = LocalDate.now().plusDays(3);
        List<?> proximas = inventarioService.listarPorCaducar(limite);

        if (!proximas.isEmpty()) {
            System.out.println("⚠️ Alertas de piso de ventas: productos por caducar → " + proximas.size());
            // Aquí podrías enviar correo, notificación, etc.
        }
    }
}