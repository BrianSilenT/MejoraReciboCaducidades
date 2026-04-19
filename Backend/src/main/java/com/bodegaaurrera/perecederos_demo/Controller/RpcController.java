package com.bodegaaurrera.perecederos_demo.Controller;

import com.bodegaaurrera.perecederos_demo.DTO.ApiResponse;
import com.bodegaaurrera.perecederos_demo.DTO.RpcResumenDTO;
import com.bodegaaurrera.perecederos_demo.Model.RpcControl;
import com.bodegaaurrera.perecederos_demo.Service.RpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/rpc")
public class RpcController {

    private final RpcService rpcService;



    // 1. Corregir el Resumen (Incompatible types)
    @GetMapping("/resumen")
    public ApiResponse<RpcResumenDTO> obtenerResumen() { // <-- Cambiado de Map a RpcResumenDTO
        return new ApiResponse<>(rpcService.obtenerResumen());
    }
    // BANDEJA 1: Lo que está en Cedis/Tienda (Enviado pero no vuelto)
    @GetMapping("/pendientes")
    public ApiResponse<List<RpcControl>> obtenerPendientes() {
        return new ApiResponse<>(rpcService.obtenerPendientes());
    }

    // 2. Corregir los Completados (Cannot resolve method)
    @GetMapping("/completados")
    public ApiResponse<List<RpcControl>> obtenerCompletados() {
        return new ApiResponse<>(rpcService.obtenerCompletados());
    }

    // ACCIÓN: El movimiento de "Enviado" -> "Recibido"
    @PutMapping("/retorno/{idRpc}")
    public ApiResponse<RpcControl> registrarRetorno(@PathVariable Long idRpc, @RequestParam int cantidadRetornada) {
        return new ApiResponse<>(rpcService.registrarRetorno(idRpc, cantidadRetornada));
    }
}
