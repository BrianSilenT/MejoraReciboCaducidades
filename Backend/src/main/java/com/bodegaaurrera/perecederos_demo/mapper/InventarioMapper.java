package com.bodegaaurrera.perecederos_demo.mapper;

import com.bodegaaurrera.perecederos_demo.DTO.AlertaInventario;
import com.bodegaaurrera.perecederos_demo.DTO.InventarioDTO;
import com.bodegaaurrera.perecederos_demo.Model.Inventario;
import org.springframework.stereotype.Component;

@Component
public class InventarioMapper {


    public static InventarioDTO toDTO(Inventario inv) {
        InventarioDTO dto = new InventarioDTO();

        if (inv.getProducto() != null) {
            dto.setCodigoBarras(inv.getProducto().getCodigoBarras());
            dto.setDescripcion(inv.getProducto().getDescripcion());
        }

        dto.setCantidad(inv.getCantidad());
        dto.setLote(inv.getLote());
        dto.setFechaCaducidad(inv.getFechaCaducidad());

        return dto;
    }

    public static AlertaInventario toAlertaDTO(Inventario inv) {

        AlertaInventario dto = new AlertaInventario();

        dto.setCodigoBarras(inv.getProducto().getCodigoBarras());
        dto.setDescripcion(inv.getProducto().getDescripcion());
        dto.setCantidad(inv.getCantidad());
        dto.setFechaCaducidad(inv.getFechaCaducidad());

        return dto;
    }
}