package com.bodegaaurrera.perecederos_demo.mapper;

import com.bodegaaurrera.perecederos_demo.DTO.ProductoDTO;
import com.bodegaaurrera.perecederos_demo.Model.Producto;

public class ProductoMapper {

    public static ProductoDTO toDTO(Producto p) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(p.getIdProducto());
        dto.setNombre(p.getNombre());
        dto.setCodigoBarras(p.getCodigoBarras());
        dto.setDescripcion(p.getDescripcion());

        // Fix: null-check antes de .name() — productos sin clasificar no explotan
        dto.setDepartamento(p.getDepartamento() != null ? p.getDepartamento().name() : null);
        dto.setDivision(p.getDivision() != null ? p.getDivision().name() : null);

        return dto;
    }
}