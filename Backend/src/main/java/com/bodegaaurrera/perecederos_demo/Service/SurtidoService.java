package com.bodegaaurrera.perecederos_demo.Service;


import com.bodegaaurrera.perecederos_demo.DTO.*;
import com.bodegaaurrera.perecederos_demo.Enums.TipoAlerta;
import com.bodegaaurrera.perecederos_demo.Enums.TipoMovimiento;
import com.bodegaaurrera.perecederos_demo.Enums.Ubicacion;
import com.bodegaaurrera.perecederos_demo.Model.Inventario;
import com.bodegaaurrera.perecederos_demo.Model.Producto;
import com.bodegaaurrera.perecederos_demo.Repository.InventarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SurtidoService {

    private final InventarioRepository inventarioRepository;
    private final AlertaService alertaService;
    private final MovimientoInventarioService movimientoService;
    private final SugerenciaSurtidoService sugerenciaService;


    public SugerenciaSurtidoDTO sugerirSurtido(String upc, int cantidadSolicitada) {

        List<Inventario> inventarios =
                inventarioRepository.findDetalleCompleto(upc);

        if (inventarios.isEmpty()) {
            throw new IllegalArgumentException("Sin inventario");
        }

        // 🔥 Obtener alertas SOLO de este producto
        List<AlertaInventario> alertas = alertaService.generarAlertas().stream()
                .filter(a -> a.getCodigoBarras().equals(upc))
                .toList();

        // 🔥 Mapear alerta por lote
        Map<String, TipoAlerta> mapaAlertas = alertas.stream()
                .collect(Collectors.toMap(
                        AlertaInventario::getLote,
                        AlertaInventario::getTipo,
                        (a, b) -> a
                ));

        // 🔥 Filtrar inventario en bodega
        List<Inventario> disponibles = inventarios.stream()
                .filter(i -> i.getUbicacion() == Ubicacion.BODEGA)
                .filter(i -> i.getFechaCaducidad() != null)
                .sorted(Comparator.comparing(Inventario::getFechaCaducidad))
                .toList();

        int restante = cantidadSolicitada;
        List<LoteDTO> sugeridos = new ArrayList<>();

        TipoAlerta tipoGeneral = TipoAlerta.OK;

        for (Inventario inv : disponibles) {

            if (restante <= 0) break;

            int tomar = Math.min(inv.getCantidad(), restante);

            TipoAlerta tipo = mapaAlertas.getOrDefault(
                    inv.getLote(),
                    TipoAlerta.OK
            );

            // 🔥 prioridad global
            if (tipo == TipoAlerta.URGENTE) {
                tipoGeneral = TipoAlerta.URGENTE;
            } else if (tipo == TipoAlerta.SUGERENCIA &&
                    tipoGeneral != TipoAlerta.URGENTE) {
                tipoGeneral = TipoAlerta.SUGERENCIA;
            }

            long dias = ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    inv.getFechaCaducidad()
            );

            LoteDTO dto = new LoteDTO();
            dto.setLote(inv.getLote());
            dto.setCantidad(tomar);
            dto.setFechaCaducidad(inv.getFechaCaducidad());
            dto.setDiasRestantes(dias);
            dto.setEstado(tipo.name());

            sugeridos.add(dto);

            restante -= tomar;
        }

        Producto producto = disponibles.get(0).getProducto();

        SugerenciaSurtidoDTO response = new SugerenciaSurtidoDTO();
        response.setCodigoBarras(producto.getCodigoBarras());
        response.setDescripcion(producto.getNombre());
        response.setCantidadSolicitada(cantidadSolicitada);
        response.setTipo(tipoGeneral);
        response.setLotesSugeridos(sugeridos);
        sugerenciaService.guardarSugerencia(upc, sugeridos);

        return response;
    }

    @Transactional
    public void ejecutarSurtido(MovimientoInventarioDTO request) {

        List<Inventario> inventarios =
                inventarioRepository.findDetalleCompleto(request.getUpc());

        if (inventarios.isEmpty()) {
            throw new IllegalArgumentException("Producto no encontrado");
        }

        for (LoteMovimientoDTO loteReq : request.getLotes()) {

            Inventario origen = inventarios.stream()
                    .filter(i -> i.getLote().equals(loteReq.getLote()))
                    .filter(i -> i.getUbicacion() == Ubicacion.BODEGA)
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalArgumentException("Lote no encontrado en bodega: " + loteReq.getLote())
                    );

            if (origen.getCantidad() < loteReq.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente en lote: " + loteReq.getLote());
            }

            // 🔥 DESCONTAR EN BODEGA
            origen.setCantidad(origen.getCantidad() - loteReq.getCantidad());

            // 🔥 REGISTRAR MOVIMIENTO
            movimientoService.registrarMovimiento(
                    origen,
                    loteReq.getCantidad(),
                    TipoMovimiento.SURTIDO,
                    Ubicacion.BODEGA,
                    Ubicacion.PISO_VENTA,
                    request.getUpc(),
                    "SYSTEM",
                    "Surtido a piso de venta"
            );

            // 🔥 BUSCAR SI YA EXISTE EN PISO_VENTA
            Optional<Inventario> destinoOpt = inventarios.stream()
                    .filter(i -> i.getLote().equals(loteReq.getLote()))
                    .filter(i -> i.getUbicacion() == Ubicacion.PISO_VENTA)
                    .findFirst();

            if (destinoOpt.isPresent()) {

                Inventario destino = destinoOpt.get();
                destino.setCantidad(destino.getCantidad() + loteReq.getCantidad());

                inventarioRepository.save(destino);

            } else {

                Inventario nuevo = crearInventarioPisoVenta(loteReq, origen);
                inventarioRepository.save(nuevo);
            }

            // 🔥 guardar origen actualizado
            inventarioRepository.save(origen);
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
