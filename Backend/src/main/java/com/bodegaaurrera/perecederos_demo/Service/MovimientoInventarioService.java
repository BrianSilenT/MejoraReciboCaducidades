package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.Enums.TipoMovimiento;
import com.bodegaaurrera.perecederos_demo.Enums.Ubicacion;
import com.bodegaaurrera.perecederos_demo.Model.Inventario;
import com.bodegaaurrera.perecederos_demo.Model.MovimientoInventario;
import com.bodegaaurrera.perecederos_demo.Repository.MovimientoInventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
            String motivo
    ) {

        // 🔐 Obtener usuario desde JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String usuario = (auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getName()))
                ? auth.getName()
                : "SYSTEM";

        // 📦 Crear movimiento
        MovimientoInventario mov = new MovimientoInventario();

        mov.setProducto(inv.getProducto());
        mov.setLote(inv.getLote());
        mov.setCantidad(cantidad);
        mov.setTipoMovimiento(tipo);
        mov.setUbicacionOrigen(origen);
        mov.setUbicacionDestino(destino);
        mov.setReferencia(referencia);
        mov.setMotivo(motivo);
        mov.setUsuario(usuario);
        mov.setFechaCaducidad(inv.getFechaCaducidad());

        repository.save(mov);
    }
}