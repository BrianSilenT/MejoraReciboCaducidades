package com.bodegaaurrera.perecederos_demo.DTO;

import lombok.Data;


import java.math.BigDecimal;
import java.util.List;

@Data
public class InventarioDetalleDTO {

        private ProductoDTO producto;

        private BigDecimal inventarioIp;
        private BigDecimal pisoVenta;
        private BigDecimal bodega;

        private List<LoteDTO> lotes;

}
