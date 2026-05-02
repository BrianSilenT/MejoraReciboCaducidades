package com.bodegaaurrera.perecederos_demo.Service;


import com.bodegaaurrera.perecederos_demo.DTO.*;
import com.bodegaaurrera.perecederos_demo.Enums.TipoMovimiento;
import com.bodegaaurrera.perecederos_demo.Enums.Ubicacion;
import com.bodegaaurrera.perecederos_demo.Model.Inventario;
import com.bodegaaurrera.perecederos_demo.Model.Producto;
import com.bodegaaurrera.perecederos_demo.Repository.InventarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;


@RequiredArgsConstructor
@Service
public class SurtidoService {

    private final InventarioRepository inventarioRepository;
    private final AlertaService alertaService;
    private final MovimientoInventarioService movimientoService;
    private final SugerenciaSurtidoService sugerenciaService;
    private final ProductoAliasService aliasService;


    public SugerenciaSurtidoDTO sugerirSurtido(String upc, BigDecimal cantidadSolicitada) {

        List<Inventario> inventarios =
                inventarioRepository.findDetalleCompleto(upc);

        if (inventarios.isEmpty()) {
            throw new IllegalArgumentException("Sin inventario");
        }

        List<Inventario> disponibles = inventarios.stream()
                .filter(i -> i.getUbicacion() == Ubicacion.BODEGA)
                .filter(i -> i.getFechaCaducidad() != null)
                .sorted(Comparator.comparing(Inventario::getFechaCaducidad))
                .toList();

        BigDecimal restante = cantidadSolicitada;
        List<LoteDTO> sugeridos = new ArrayList<>();

        for (Inventario inv : disponibles) {

            if (restante.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal disponible = inv.getCantidad();

            if (disponible == null || disponible.compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal tomar = disponible.min(restante);

            LoteDTO dto = new LoteDTO();
            dto.setLote(inv.getLote());
            dto.setCantidad(tomar);
            dto.setFechaCaducidad(inv.getFechaCaducidad());

            sugeridos.add(dto);

            restante = restante.subtract(tomar);
        }

        Producto producto = disponibles.get(0).getProducto();

        SugerenciaSurtidoDTO response = new SugerenciaSurtidoDTO();
        response.setCodigoBarras(producto.getCodigoBarras());
        response.setDescripcion(producto.getNombre());
        response.setCantidadSolicitada(cantidadSolicitada);
        response.setLotesSugeridos(sugeridos);

        return response;
    }

    @Transactional
    public void ejecutarSurtido(MovimientoInventarioDTO request) {

        String upcNormalizado = aliasService.resolverUpcInventario(request.getUpc());

        List<Inventario> inventarios =
                inventarioRepository.findDetalleCompleto(upcNormalizado);

        if (inventarios.isEmpty()) {
            throw new IllegalArgumentException("Producto no encontrado");
        }

        for (LoteMovimientoDTO loteReq : request.getLotes()) {
            movimientoService.ejecutarMovimiento(
                    upcNormalizado,
                    loteReq.getLote(),
                    loteReq.getCantidad(),
                    TipoMovimiento.SURTIDO,
                    Ubicacion.BODEGA,
                    Ubicacion.PISO_VENTA,
                    request.getUpc(),
                    "Surtido a piso"
            );
        }
    }

    private Inventario crearInventarioPisoVenta(LoteMovimientoDTO loteReq, Inventario origen) {

        Inventario nuevo = new Inventario();

        nuevo.setProducto(origen.getProducto());
        nuevo.setCantidad(loteReq.getCantidad());
        nuevo.setLote(origen.getLote());
        nuevo.setFechaCaducidad(origen.getFechaCaducidad());
        nuevo.setFechaLlegada(origen.getFechaLlegada());
        nuevo.setDepartamento(origen.getDepartamento());
        nuevo.setDivision(origen.getDivision());
        nuevo.setUbicacion(Ubicacion.PISO_VENTA);

        return nuevo;
    }
}
