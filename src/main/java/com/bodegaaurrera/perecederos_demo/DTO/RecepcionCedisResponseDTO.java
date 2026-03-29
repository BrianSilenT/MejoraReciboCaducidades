package com.bodegaaurrera.perecederos_demo.DTO;


import com.bodegaaurrera.perecederos_demo.Model.RecepcionCedis;
import com.bodegaaurrera.perecederos_demo.Model.RecepcionCedisDetalle;
import com.bodegaaurrera.perecederos_demo.Model.DiscrepanciaRecepcion;
import com.bodegaaurrera.perecederos_demo.Model.RpcControl;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RecepcionCedisResponseDTO {
    private RecepcionCedis recepcion;
    private List<RecepcionCedisDetalle> detalles;
    private List<DiscrepanciaRecepcion> discrepancias;
    private List<RpcControl> rpc;
}