package com.bodegaaurrera.perecederos_demo.Controller;

import com.bodegaaurrera.perecederos_demo.Model.ApiResponse;
import com.bodegaaurrera.perecederos_demo.Model.RpcControl;
import com.bodegaaurrera.perecederos_demo.Service.RpcService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/rpc")
public class RpcController {

    private final RpcService rpcService;

    public RpcController(RpcService rpcService) {
        this.rpcService = rpcService;
    }

    @PostMapping("/entrega")
    public ApiResponse<RpcControl> registrarEntrega(@RequestBody RpcControl rpc) {
        RpcControl nuevo = rpcService.registrarEntrega(rpc);
        return new ApiResponse<>(nuevo);
    }

    @PutMapping("/retorno/{idRpc}")
    public ApiResponse<RpcControl> registrarRetorno(@PathVariable Long idRpc, @RequestParam int cantidadRetornada) {
        RpcControl actualizado = rpcService.registrarRetorno(idRpc, cantidadRetornada);
        return new ApiResponse<>(actualizado);
    }

    @GetMapping("/pendientes")
    public ApiResponse<List<RpcControl>> obtenerPendientes() {
        List<RpcControl> pendientes = rpcService.obtenerPendientes();
        return new ApiResponse<>(pendientes);
    }

    @GetMapping("/completados")
    public ApiResponse<List<RpcControl>> obtenerCompletados() {
        List<RpcControl> completados = rpcService.obtenerCompletados();
        return new ApiResponse<>(completados);
    }

    @GetMapping("/camion/{numeroCamion}")
    public ApiResponse<List<RpcControl>> obtenerPorCamion(@PathVariable String numeroCamion) {
        List<RpcControl> rpcPorCamion = rpcService.obtenerPorCamion(numeroCamion);
        return new ApiResponse<>(rpcPorCamion);
    }
}