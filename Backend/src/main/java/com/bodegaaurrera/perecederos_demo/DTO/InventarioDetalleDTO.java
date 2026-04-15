package com.bodegaaurrera.perecederos_demo.DTO;

import lombok.Data;


import java.util.List;

@Data
public class InventarioDetalleDTO {

        private ProductoDTO producto;

        private int inventarioIp;
        private int pisoVenta;
        private int bodega;

        private List<LoteDTO> lotes;

}
