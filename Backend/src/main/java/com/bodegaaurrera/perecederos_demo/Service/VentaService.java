package com.bodegaaurrera.perecederos_demo.Service;


import com.bodegaaurrera.perecederos_demo.Enums.TipoMovimiento;
import com.bodegaaurrera.perecederos_demo.Enums.Ubicacion;
import com.bodegaaurrera.perecederos_demo.Model.Inventario;
import com.bodegaaurrera.perecederos_demo.Repository.InventarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class VentaService {

    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioService movimientoService;
    private final ProductoAliasService aliasService;

    @Transactional
    public void registrarVenta(String upcEscaneado, BigDecimal cantidadVendida) {

        if (cantidadVendida.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }

        String upcInventario = aliasService.resolverUpcInventario(upcEscaneado);

        List<Inventario> inventarios = inventarioRepository
                .findDetalleCompleto(upcInventario)
                .stream()
                .filter(i -> i.getUbicacion() == Ubicacion.PISO_VENTA)
                .filter(i -> i.getCantidad().compareTo(BigDecimal.ZERO) > 0)
                .sorted(Comparator.comparing(Inventario::getFechaCaducidad))
                .toList();

        if (inventarios.isEmpty()) {
            throw new IllegalArgumentException("Sin inventario en piso");
        }

        BigDecimal restante = cantidadVendida;

        for (Inventario inv : inventarios) {

            if (restante.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal disponible = inv.getCantidad();

            BigDecimal aDescontar = disponible.min(restante);

            movimientoService.ejecutarMovimiento(
                    upcInventario,
                    inv.getLote(),
                    aDescontar,
                    TipoMovimiento.VENTA_MANUAL,
                    Ubicacion.PISO_VENTA,
                    null,
                    upcEscaneado,
                    "Venta POS"
            );

            restante = restante.subtract(aDescontar);
        }

        if (restante.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("Stock insuficiente");
        }
    }
}
