package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.Model.*;
import com.bodegaaurrera.perecederos_demo.Repository.InventarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;

    private static final Map<String, Integer> POLITICAS_RETIRO = Map.of(
            "Farmacia", 30,
            "Perecederos", 5,
            "Embutidos", 10,
            "Lacteos", 7
    );

    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    // ✅ Listar todo el inventario
    public List<Inventario> listarTodo() {
        return inventarioRepository.findAll();
    }

    // ✅ Registrar nuevo inventario
    public Inventario registrar(Inventario inventario) {
        return inventarioRepository.save(inventario);
    }

    // ✅ Consultar inventario por código de barras (varios lotes)
    public List<Inventario> obtenerPorCodigoBarras(String codigoBarras) {
        return inventarioRepository.findByCodigoBarras(codigoBarras);
    }

    // ✅ Cargar inventario desde RecepcionDetalle
    public void cargarInventarioDesdeRecepcion(RecepcionDetalle detalle) {
        Inventario inv = inventarioRepository
                .findByCodigoBarrasAndLote(
                        detalle.getProducto().getCodigoBarras(),
                        detalle.getLote()
                )
                .orElse(new Inventario());

        inv.setCodigoBarras(detalle.getProducto().getCodigoBarras());
        inv.setDescripcion(detalle.getProducto().getDescripcion());
        inv.setLote(detalle.getLote());
        inv.setFechaCaducidad(detalle.getFechaCaducidad());
        inv.setFechaLlegada(LocalDate.now());
        inv.setCantidad(inv.getCantidad() + detalle.getCantidadRecibida());

        // 🔹 Si el producto tiene división/departamento, asignarlos
        if (detalle.getProducto().getDivision() != null) {
            inv.setDivision(detalle.getProducto().getDivision());
        }
        if (detalle.getProducto().getDepartamento() != null) {
            inv.setDepartamento(detalle.getProducto().getDepartamento());
        }

        inventarioRepository.save(inv);
    }

    // ✅ Cargar inventario desde recepción de CEDIS
    public void cargarInventarioDesdeRecepcionCedis(RecepcionCedis recepcion, List<Inventario> itemsRecibidos) {
        for (Inventario item : itemsRecibidos) {
            item.setDivision(recepcion.getDivision());
            item.setDepartamento(recepcion.getDepartamento());
            item.setFechaLlegada(LocalDate.now());
            inventarioRepository.save(item);
        }
    }

    // ✅ Listar productos próximos a caducar
    public List<Inventario> listarPorCaducar(LocalDate limite) {
        return inventarioRepository.findByFechaCaducidadBefore(limite);
    }

    // ✅ Listar inventario por división
    public List<Inventario> obtenerPorDivision(String division) {
        return inventarioRepository.findAll().stream()
                .filter(inv -> inv.getDivision() != null &&
                        inv.getDivision().name().equalsIgnoreCase(division))
                .toList();
    }

    // ✅ Listar inventario por departamento
    public List<Inventario> obtenerPorDepartamento(String departamento) {
        return inventarioRepository.findAll().stream()
                .filter(inv -> inv.getDepartamento() != null &&
                        inv.getDepartamento().name().equalsIgnoreCase(departamento))
                .toList();
    }

    // ✅ Generar alertas automáticas de caducidad
    public List<AlertaInventario> generarAlertasCaducidad() {
        LocalDate hoy = LocalDate.now();
        List<Inventario> todos = inventarioRepository.findAll();

        return todos.stream().map(inv -> {
            int diasPolitica = POLITICAS_RETIRO.entrySet().stream()
                    .filter(e -> inv.getDescripcion() != null &&
                            inv.getDescripcion().toLowerCase().contains(e.getKey().toLowerCase()))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(5);

            long diasRestantes;
            String alerta;

            if (inv.getFechaCaducidad() == null) {
                diasRestantes = -1;
                alerta = "⚠️ Fecha de caducidad no registrada";
            } else {
                diasRestantes = ChronoUnit.DAYS.between(hoy, inv.getFechaCaducidad());

                if (diasRestantes <= 0) {
                    alerta = "❌ Producto vencido, retirar inmediatamente";
                } else if (diasRestantes <= diasPolitica) {
                    alerta = "⚠️ Retirar mercancía, próxima a vencer";
                } else {
                    alerta = "✅ Producto en buen estado";
                }
            }

            AlertaInventario dto = new AlertaInventario();
            dto.setCodigoBarras(inv.getCodigoBarras());
            dto.setDescripcion(inv.getDescripcion());
            dto.setCantidad(inv.getCantidad());
            dto.setFechaCaducidad(inv.getFechaCaducidad());
            dto.setDiasRestantes(diasRestantes);
            dto.setAlerta(alerta);

            return dto;
        }).toList();
    }
}