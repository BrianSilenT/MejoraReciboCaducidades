package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.Enums.TipoMovimiento;
import com.bodegaaurrera.perecederos_demo.Enums.Ubicacion;
import com.bodegaaurrera.perecederos_demo.Model.Inventario;
import com.bodegaaurrera.perecederos_demo.Model.MovimientoInventario;
import com.bodegaaurrera.perecederos_demo.Model.Producto;
import com.bodegaaurrera.perecederos_demo.Repository.InventarioRepository;
import com.bodegaaurrera.perecederos_demo.Repository.MovimientoInventarioRepository;
import com.bodegaaurrera.perecederos_demo.Repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class MovimientoInventarioService {

    private final InventarioRepository inventarioRepo;
    private final MovimientoInventarioRepository movimientoRepo;
    private final ProductoRepository productoRepository;

    private BigDecimal safe(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val;
    }

    @Transactional
    public void ejecutarMovimiento(
            String upc,
            String lote,
            BigDecimal cantidad,
            TipoMovimiento tipo,
            Ubicacion origen,
            Ubicacion destino,
            String referencia,
            String motivo
    ) {

        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuario = (auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getName()))
                ? auth.getName()
                : "SYSTEM";

        Inventario invOrigen = null;

        if (origen != null) {
            invOrigen = inventarioRepo
                    .findByProductoCodigoBarrasAndLoteAndUbicacion(upc, lote, origen)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Inventario no encontrado en origen: " + origen));
        }

        switch (tipo) {

            // =========================
            // RECEPCION
            // =========================
            case RECEPCION -> {

                Inventario destinoInv = buscarODefinirDestino(upc, lote, destino, null);

                destinoInv.setCantidad(
                        safe(destinoInv.getCantidad()).add(cantidad)
                );

                inventarioRepo.save(destinoInv);

                guardarMovimiento(destinoInv, cantidad, tipo, origen, destino, referencia, motivo, usuario);
            }

            // =========================
            // SURTIDO (BODEGA → PISO)
            // =========================
            case SURTIDO -> {

                if (invOrigen == null) {
                    throw new IllegalArgumentException("Origen requerido para surtido");
                }

                validarStock(invOrigen, cantidad);

                invOrigen.setCantidad(
                        safe(invOrigen.getCantidad()).subtract(cantidad)
                );

                inventarioRepo.save(invOrigen);

                Inventario destinoInv = buscarODefinirDestino(upc, lote, destino, invOrigen);

                destinoInv.setCantidad(
                        safe(destinoInv.getCantidad()).add(cantidad)
                );

                inventarioRepo.save(destinoInv);

                guardarMovimiento(invOrigen, cantidad, tipo, origen, destino, referencia, motivo, usuario);
            }

            // =========================
            // VENTA / MERMA (sale del sistema)
            // =========================
            case VENTA_MANUAL, MERMA -> {

                if (invOrigen == null) {
                    throw new IllegalArgumentException("Origen requerido");
                }

                validarStock(invOrigen, cantidad);

                invOrigen.setCantidad(
                        safe(invOrigen.getCantidad()).subtract(cantidad)
                );

                inventarioRepo.save(invOrigen);

                guardarMovimiento(invOrigen, cantidad, tipo, origen, null, referencia, motivo, usuario);
            }

            // =========================
            // AJUSTE
            // =========================
            case AJUSTE -> {

                if (invOrigen == null) {
                    throw new IllegalArgumentException("Origen requerido");
                }

                BigDecimal nuevaCantidad = safe(invOrigen.getCantidad()).add(cantidad);

                if (nuevaCantidad.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Ajuste deja inventario negativo");
                }

                invOrigen.setCantidad(nuevaCantidad);
                inventarioRepo.save(invOrigen);

                guardarMovimiento(invOrigen, cantidad, tipo, origen, null, referencia, motivo, usuario);
            }
        }
    }

    //metodos auxiliares
    private void validarStock(Inventario inv, BigDecimal cantidad) {
        if (safe(inv.getCantidad()).compareTo(cantidad) < 0) {
            throw new IllegalArgumentException("Stock insuficiente");
        }
    }

    private Inventario buscarODefinirDestino(
            String upc,
            String lote,
            Ubicacion destino,
            Inventario origen
    ) {

        return inventarioRepo
                .findByProductoCodigoBarrasAndLoteAndUbicacion(upc, lote, destino)
                .orElseGet(() -> {

                    Inventario nuevo = new Inventario();

                    if (origen != null) {
                        nuevo.setProducto(origen.getProducto());
                        nuevo.setFechaCaducidad(origen.getFechaCaducidad());
                        nuevo.setFechaLlegada(origen.getFechaLlegada());
                        nuevo.setDepartamento(origen.getDepartamento());
                        nuevo.setDivision(origen.getDivision());
                    } else {
                        // 🔥 RECEPCIÓN
                        Producto producto = productoRepository
                                .findByCodigoBarras(upc)
                                .orElseThrow(() -> new IllegalArgumentException("Producto no existe"));

                        nuevo.setProducto(producto);
                    }


                    nuevo.setLote(lote);
                    nuevo.setUbicacion(destino);
                    nuevo.setCantidad(BigDecimal.ZERO);

                    return nuevo;
                });
    }

    private void guardarMovimiento(
            Inventario inv,
            BigDecimal cantidad,
            TipoMovimiento tipo,
            Ubicacion origen,
            Ubicacion destino,
            String referencia,
            String motivo,
            String usuario
    ) {

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

        movimientoRepo.save(mov);
    }
}