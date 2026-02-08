package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.Model.RpcControl;
import com.bodegaaurrera.perecederos_demo.Repository.RpcControlRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RpcService {

    private final RpcControlRepository rpcRepository;

    public RpcService(RpcControlRepository rpcRepository) {
        this.rpcRepository = rpcRepository;
    }

    public RpcControl registrarEntrega(RpcControl rpc) {
        rpc.setFechaRegistro(LocalDate.now());
        rpc.setPendienteRetorno(true);
        return rpcRepository.save(rpc);
    }

    public RpcControl registrarRetorno(Long idRpc, int cantidadRetornada) {
        RpcControl rpc = rpcRepository.findById(idRpc)
                .orElseThrow(() -> new RuntimeException("RPC no encontrado"));
        rpc.setCantidadRetornada(cantidadRetornada);
        rpc.setPendienteRetorno(rpc.getCantidadEntregada() > cantidadRetornada);
        return rpcRepository.save(rpc);
    }

    public List<RpcControl> obtenerPendientes() {
        return rpcRepository.findByPendienteRetorno(true);
    }

    public List<RpcControl> obtenerPorCamion(String numeroCamion) {
        return rpcRepository.findByNumeroCamion(numeroCamion);
    }
}
