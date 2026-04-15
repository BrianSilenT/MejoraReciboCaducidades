package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.Enums.TipoMovimiento;
import com.bodegaaurrera.perecederos_demo.Enums.Ubicacion;
import com.bodegaaurrera.perecederos_demo.Model.Inventario;
import com.bodegaaurrera.perecederos_demo.Model.MovimientoInventario;
import com.bodegaaurrera.perecederos_demo.Repository.MovimientoInventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class MovimientoInventarioService {

    private final MovimientoInventarioRepository repository;


    public void registrarMovimiento(
            Inventario inv,
            int cantidad,
            TipoMovimiento tipo,
            Ubicacion origen,
            Ubicacion destino,
            String referencia,
            String usuario,
            String motivo
    ) {

        MovimientoInventario mov = new MovimientoInventario();

        mov.setProducto(inv.getProducto());
        mov.setLote(inv.getLote());
        mov.setCantidad(cantidad);
        mov.setTipoMovimiento(tipo);
        mov.setUbicacionOrigen(origen);
        mov.setUbicacionDestino(destino);
        mov.setReferencia(referencia);
        mov.setUsuario(usuario);
        mov.setMotivo(motivo);
        mov.setFecha(LocalDateTime.now());

        repository.save(mov);
    }
}