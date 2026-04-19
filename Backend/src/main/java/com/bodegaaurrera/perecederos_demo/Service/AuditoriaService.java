package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.DTO.AuditoriaSurtidoDTO;
import com.bodegaaurrera.perecederos_demo.Enums.TipoMovimiento;
import com.bodegaaurrera.perecederos_demo.Enums.Ubicacion;
import com.bodegaaurrera.perecederos_demo.Model.Inventario;
import com.bodegaaurrera.perecederos_demo.Model.MovimientoInventario;
import com.bodegaaurrera.perecederos_demo.Repository.InventarioRepository;
import com.bodegaaurrera.perecederos_demo.Repository.MovimientoInventarioRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AuditoriaService {

    private final MovimientoInventarioRepository movimientoRepo;
    private final InventarioRepository inventarioRepo;

    public AuditoriaService(MovimientoInventarioRepository movimientoRepo,
                            InventarioRepository inventarioRepo) {
        this.movimientoRepo = movimientoRepo;
        this.inventarioRepo = inventarioRepo;
    }

    public List<AuditoriaSurtidoDTO> auditarSurtido() {

        List<MovimientoInventario> movimientos =
                movimientoRepo.findAll().stream()
                        .filter(m -> m.getTipoMovimiento() == TipoMovimiento.SURTIDO)
                        .toList();

        List<AuditoriaSurtidoDTO> resultado = new ArrayList<>();

        for (MovimientoInventario mov : movimientos) {

            // 🔥 inventario disponible en ese momento (simplificado)
            List<Inventario> inventarios =
                    inventarioRepo.findDetalleCompleto(
                                    mov.getProducto().getCodigoBarras()
                            ).stream()
                            .filter(i -> i.getUbicacion() == Ubicacion.BODEGA)
                            .filter(i -> i.getFechaCaducidad() != null)
                            .sorted(Comparator.comparing(Inventario::getFechaCaducidad))
                            .toList();

            if (inventarios.isEmpty()) continue;

            final AuditoriaSurtidoDTO dto = getAuditoriaSurtidoDTO(mov, inventarios);

            resultado.add(dto);
        }

        return resultado;
    }

    private static AuditoriaSurtidoDTO getAuditoriaSurtidoDTO(MovimientoInventario mov, List<Inventario> inventarios) {
        String loteCorrecto = inventarios.get(0).getLote(); // FEFO

        boolean error = !loteCorrecto.equals(mov.getLote());

        AuditoriaSurtidoDTO dto = new AuditoriaSurtidoDTO();

        dto.setCodigoBarras(mov.getProducto().getCodigoBarras());
        dto.setLoteCorrecto(loteCorrecto);
        dto.setLoteSurtido(mov.getLote());
        dto.setUsuario(mov.getUsuario());
        dto.setCantidad(mov.getCantidad());
        dto.setErrorFEFO(error);

        if (error) {
            dto.setMensaje("❌ No respetó FEFO");
        } else {
            dto.setMensaje("✅ Correcto");
        }
        return dto;
    }
}
