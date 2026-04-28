package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.DTO.AlertaInventario;
import com.bodegaaurrera.perecederos_demo.DTO.InventarioDTO;
import com.bodegaaurrera.perecederos_demo.DTO.InventarioDetalleDTO;
import com.bodegaaurrera.perecederos_demo.DTO.LoteDTO;
import com.bodegaaurrera.perecederos_demo.Enums.Departamento;
import com.bodegaaurrera.perecederos_demo.Enums.Division;
import com.bodegaaurrera.perecederos_demo.Enums.Ubicacion;
import com.bodegaaurrera.perecederos_demo.Model.*;
import com.bodegaaurrera.perecederos_demo.Repository.InventarioRepository;
import com.bodegaaurrera.perecederos_demo.mapper.InventarioMapper;
import com.bodegaaurrera.perecederos_demo.mapper.ProductoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;

    public List<InventarioDTO> listarTodo() {
        return inventarioRepository.findAll()
                .stream()
                .map(InventarioMapper::toDTO)
                .toList();
    }

    // Registrar nuevo inventario
    public Inventario registrar(Inventario inventario) {
        return inventarioRepository.save(inventario);
    }

    public List<InventarioDTO> listarCaducados() {
        return inventarioRepository
                .findByFechaCaducidadBefore(LocalDate.now())
                .stream()
                .map(InventarioMapper::toDTO)
                .toList();
    }

    public List<InventarioDTO> listarPorCaducar(LocalDate limite) {
        return inventarioRepository
                .findByFechaCaducidadBetween(LocalDate.now(), limite)
                .stream()
                .map(InventarioMapper::toDTO)
                .toList();
    }

    public List<InventarioDTO> obtenerPorDivision(String division) {
        return inventarioRepository.findByDivision(
                        Division.valueOf(division.toUpperCase())
                ).stream()
                .map(InventarioMapper::toDTO)
                .toList();
    }

    public List<InventarioDTO> obtenerPorDepartamento(String departamento) {
        return inventarioRepository.findByDepartamento(
                        Departamento.valueOf(departamento.toUpperCase())
                ).stream()
                .map(InventarioMapper::toDTO)
                .toList();
    }

    // Generar alertas automáticas de caducidad (REFACTORINGS)
    public List<AlertaInventario> generarAlertasCaducidad() {
        return inventarioRepository.findAll()
                .stream()
                .map(InventarioMapper::toAlertaDTO)
                .toList();
    }

    public InventarioDetalleDTO obtenerDetallePorUpc(String upc) {

        List<Inventario> inventarios =
                inventarioRepository.findDetalleCompleto(upc);

        if (inventarios.isEmpty()) {
            throw new IllegalArgumentException("Producto no encontrado o sin inventario");
        }

        Producto producto = inventarios.get(0).getProducto();

        int total = 0;
        int pisoVenta = 0;
        int bodega = 0;

        Map<String, LoteDTO> mapaLotes = new HashMap<>();

        for (Inventario inv : inventarios) {

            int cantidad = inv.getCantidad();
            total += cantidad;

            // 🔥 ubicación real
            if (inv.getUbicacion() == Ubicacion.PISO_VENTA) {
                pisoVenta += cantidad;
            } else {
                bodega += cantidad;
            }

            // 🔥 key segura
            String loteStr = inv.getLote() != null ? inv.getLote() : "SIN_LOTE";
            String key = loteStr + "_" + inv.getFechaCaducidad();

            if (!mapaLotes.containsKey(key)) {
                LoteDTO dto = new LoteDTO();
                dto.setLote(loteStr);
                dto.setFechaCaducidad(inv.getFechaCaducidad());
                dto.setCantidad(0);

                // 🔥 cálculo de días
                if (inv.getFechaCaducidad() != null) {
                    long dias = ChronoUnit.DAYS.between(LocalDate.now(), inv.getFechaCaducidad());
                    dto.setDiasRestantes(dias);
                    dto.setEstado(dias <= 3 ? "ALERTA" : "OK");
                }

                mapaLotes.put(key, dto);
            }

            LoteDTO lote = mapaLotes.get(key);
            lote.setCantidad(lote.getCantidad() + cantidad);
        }

        InventarioDetalleDTO response = new InventarioDetalleDTO();

        // 🔥 USA TU MAPPER REAL
        response.setProducto(ProductoMapper.toDTO(producto));

        response.setInventarioIp(total);
        response.setPisoVenta(pisoVenta);
        response.setBodega(bodega);
        response.setLotes(new ArrayList<>(mapaLotes.values()));

        return response;
    }
}