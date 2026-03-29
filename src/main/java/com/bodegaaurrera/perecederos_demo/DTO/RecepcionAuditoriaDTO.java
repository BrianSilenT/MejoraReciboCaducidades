package com.bodegaaurrera.perecederos_demo.DTO;

import com.bodegaaurrera.perecederos_demo.Model.RecepcionCedis;
import lombok.Data;

import java.util.List;

@Data
public class RecepcionAuditoriaDTO {
    private RecepcionCedis recepcion;   // datos generales de la recepción
    private List<DetallePlanoDTO> detalles;        // lista de detalles simplificados
    private List<DiscrepanciaDTO> discrepancias;   // discrepancias registradas
    private List<RpcDTO> rpc;                      // control de RPC

}