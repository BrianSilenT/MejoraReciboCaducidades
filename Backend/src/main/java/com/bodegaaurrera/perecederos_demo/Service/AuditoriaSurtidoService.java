package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.Enums.TipoMovimiento;
import com.bodegaaurrera.perecederos_demo.Model.AuditoriaSurtido;
import com.bodegaaurrera.perecederos_demo.Model.MovimientoInventario;
import com.bodegaaurrera.perecederos_demo.Model.SugerenciaSurtido;
import com.bodegaaurrera.perecederos_demo.Repository.AuditoriaSurtidoRepository;
import com.bodegaaurrera.perecederos_demo.Repository.MovimientoInventarioRepository;
import com.bodegaaurrera.perecederos_demo.Repository.SugerenciaSurtidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuditoriaSurtidoService {

    private final SugerenciaSurtidoRepository sugerenciaRepo;
    private final MovimientoInventarioRepository movimientoRepo;
    private final AuditoriaSurtidoRepository auditoriaRepo;



    public void auditar(String upc) {

        List<SugerenciaSurtido> sugerencias = sugerenciaRepo.findByUpc(upc);

        List<MovimientoInventario> movimientos =
                movimientoRepo.findByProductoCodigoBarras(upc).stream()
                        .filter(m -> m.getTipoMovimiento() == TipoMovimiento.SURTIDO)
                        .toList();

        Map<String, Integer> sugeridoPorLote = sugerencias.stream()
                .collect(Collectors.groupingBy(
                        SugerenciaSurtido::getLote,
                        Collectors.summingInt(SugerenciaSurtido::getCantidadSugerida)
                ));

        Map<String, Integer> surtidoPorLote = movimientos.stream()
                .collect(Collectors.groupingBy(
                        MovimientoInventario::getLote,
                        Collectors.summingInt(MovimientoInventario::getCantidad)
                ));

        // 🔥 comparar
        for (String lote : sugeridoPorLote.keySet()) {

            int sugerido = sugeridoPorLote.getOrDefault(lote, 0);
            int surtido = surtidoPorLote.getOrDefault(lote, 0);

            AuditoriaSurtido a = new AuditoriaSurtido();
            a.setUpc(upc);
            a.setLote(lote);
            a.setCantidadSugerida(sugerido);
            a.setCantidadSurtida(surtido);
            a.setFecha(LocalDateTime.now());

            if (surtido == 0) {
                a.setCorrecto(false);
                a.setMotivo("No se surtió el lote sugerido");
            } else if (surtido < sugerido) {
                a.setCorrecto(false);
                a.setMotivo("Surtido incompleto");
            } else {
                a.setCorrecto(true);
                a.setMotivo("Correcto");
            }

            auditoriaRepo.save(a);
        }

        // 🔥 detectar si surtió lote incorrecto
        for (String lote : surtidoPorLote.keySet()) {

            if (!sugeridoPorLote.containsKey(lote)) {

                AuditoriaSurtido a = new AuditoriaSurtido();
                a.setUpc(upc);
                a.setLote(lote);
                a.setCantidadSugerida(0);
                a.setCantidadSurtida(surtidoPorLote.get(lote));
                a.setCorrecto(false);
                a.setMotivo("Se surtió lote no sugerido (rompió FEFO)");
                a.setFecha(LocalDateTime.now());

                auditoriaRepo.save(a);
            }
        }
    }
}
