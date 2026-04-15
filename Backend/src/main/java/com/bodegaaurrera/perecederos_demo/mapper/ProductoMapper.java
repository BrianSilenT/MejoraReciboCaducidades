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
        dto.setDepartamento(p.getDepartamento().name());
        dto.setDivision(p.getDivision().name());
        return dto;
    }
}