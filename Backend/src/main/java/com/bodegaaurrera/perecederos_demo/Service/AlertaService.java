package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.DTO.AlertaInventario;
import com.bodegaaurrera.perecederos_demo.Enums.TipoAlerta;
import com.bodegaaurrera.perecederos_demo.Model.Inventario;
import com.bodegaaurrera.perecederos_demo.Repository.InventarioRepository;
import com.bodegaaurrera.perecederos_demo.mapper.InventarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AlertaService {

    private final InventarioRepository inventarioRepository;


    public List<AlertaInventario> generarAlertas() {

        List<Inventario> inventarios = inventarioRepository.findAll();

        return inventarios.stream()
                .map(this::evaluar)
                .toList();
    }

    private AlertaInventario evaluar(Inventario inv) {

        AlertaInventario dto = InventarioMapper.toAlertaDTO(inv);

        if (inv.getFechaCaducidad() == null) {
            dto.setTipo(TipoAlerta.OK);
            return dto;
        }

        long dias = ChronoUnit.DAYS.between(LocalDate.now(), inv.getFechaCaducidad());

        dto.setDiasRestantes(dias);
        dto.setTipo(calcularTipo(dias));

        return dto;
    }

    private TipoAlerta calcularTipo(long dias) {
        if (dias < 0) return TipoAlerta.CADUCADO;
        if (dias <= 3) return TipoAlerta.URGENTE;
        if (dias <= 7) return TipoAlerta.SUGERENCIA;
        return TipoAlerta.OK;
    }
}