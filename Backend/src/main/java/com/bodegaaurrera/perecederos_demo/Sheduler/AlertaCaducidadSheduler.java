package com.bodegaaurrera.perecederos_demo.Sheduler;

import com.bodegaaurrera.perecederos_demo.DTO.AlertaInventario;
import com.bodegaaurrera.perecederos_demo.Enums.TipoAlerta;
import com.bodegaaurrera.perecederos_demo.Service.AlertaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AlertaCaducidadSheduler {

    private final AlertaService alertaService;

    public AlertaCaducidadSheduler(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void revisarPorCaducar() {

        List<AlertaInventario> alertas = alertaService.generarAlertas();

        long urgentes = alertas.stream()
                .filter(a -> a.getTipo() == TipoAlerta.URGENTE)
                .count();

        if (urgentes > 0) {
            System.out.println("⚠️ URGENTES: " + urgentes);
        }
    }
}