package com.bodegaaurrera.perecederos_demo.Controller;

import com.bodegaaurrera.perecederos_demo.Model.ApiResponse;
import com.bodegaaurrera.perecederos_demo.Model.RpcControl;
import com.bodegaaurrera.perecederos_demo.Service.RpcService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rpc")
public class RpcController {

    private final RpcService rpcService;

    public RpcController(RpcService rpcService) {
        this.rpcService = rpcService;
    }

    // DASHBOARD: Resumen de totales (KPIs)
    @GetMapping("/resumen")
    public ApiResponse<Map<String, Object>> obtenerResumen() {
        return new ApiResponse<>(rpcService.obtenerResumen());
    }

    // BANDEJA 1: Lo que está en Cedis/Tienda (Enviado pero no vuelto)
    @GetMapping("/pendientes")
    public ApiResponse<List<RpcControl>> obtenerPendientes() {
        return new ApiResponse<>(rpcService.obtenerPendientes());
    }

    // BANDEJA 2: Lo que ya regresó físicamente
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
