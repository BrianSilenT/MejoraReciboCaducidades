package com.bodegaaurrera.perecederos_demo.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RecepcionCedisResponseDTO {
    private RecepcionCedisDTO recepcion;
    private List<RecepcionCedisDetalleDTO> detalles = new ArrayList<>();
    private List<DiscrepanciaDTO> discrepancias = new ArrayList<>();
    private List<RpcDTO> rpc = new ArrayList<>();
}