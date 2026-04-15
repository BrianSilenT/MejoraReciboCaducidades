package com.bodegaaurrera.perecederos_demo.Service;


import com.bodegaaurrera.perecederos_demo.DTO.LoteDTO;
import com.bodegaaurrera.perecederos_demo.Model.SugerenciaSurtido;
import com.bodegaaurrera.perecederos_demo.Repository.SugerenciaSurtidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SugerenciaSurtidoService {

    private final SugerenciaSurtidoRepository repository;


    public void guardarSugerencia(String upc, List<LoteDTO> lotes) {

        for (LoteDTO lote : lotes) {

            SugerenciaSurtido s = new SugerenciaSurtido();

            s.setUpc(upc);
            s.setLote(lote.getLote());
            s.setCantidadSugerida(lote.getCantidad());
            s.setFechaCaducidad(lote.getFechaCaducidad());
            s.setFechaGeneracion(LocalDateTime.now());

            repository.save(s);
        }
    }
}
