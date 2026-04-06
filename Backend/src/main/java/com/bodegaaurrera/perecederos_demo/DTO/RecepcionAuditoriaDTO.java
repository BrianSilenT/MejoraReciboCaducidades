package com.bodegaaurrera.perecederos_demo.DTO;


import lombok.Data;

import java.util.List;

@Data
public class RecepcionAuditoriaDTO {
    private RecepcionCedisDTO recepcion;   // datos generales de la recepción
    private List<DetallePlanoDTO> detalles;
    private List<DiscrepanciaDTO> discrepancias;
    private List<RpcDTO> rpc;
}