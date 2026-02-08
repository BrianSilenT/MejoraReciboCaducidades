package com.bodegaaurrera.perecederos_demo.Controller;

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
    public RpcControl registrarEntrega(@RequestBody RpcControl rpc) {
        return rpcService.registrarEntrega(rpc);
    }

    @PutMapping("/retorno/{idRpc}")
    public RpcControl registrarRetorno(@PathVariable Long idRpc, @RequestParam int cantidadRetornada) {
        return rpcService.registrarRetorno(idRpc, cantidadRetornada);
    }

    @GetMapping("/pendientes")
    public List<RpcControl> obtenerPendientes() {
        return rpcService.obtenerPendientes();
    }

    @GetMapping("/camion/{numeroCamion}")
    public List<RpcControl> obtenerPorCamion(@PathVariable String numeroCamion) {
        return rpcService.obtenerPorCamion(numeroCamion);
    }
}
