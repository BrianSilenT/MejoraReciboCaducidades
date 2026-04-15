package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.DTO.RpcResumenDTO;
import com.bodegaaurrera.perecederos_demo.Model.RecepcionCedis;
import com.bodegaaurrera.perecederos_demo.Model.RecepcionCedisDetalle;
import com.bodegaaurrera.perecederos_demo.Model.RpcControl;
import com.bodegaaurrera.perecederos_demo.Repository.RpcControlRepository;
import com.bodegaaurrera.perecederos_demo.mapper.RecepcionMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RpcService {


    private final RpcControlRepository rpcRepository;
    private final RecepcionMapper rpcMapper; // ✅ Inyectado



    @Transactional
    public RpcControl registrarRetorno(Long idRpc, int cantidadRetornada) {
        RpcControl rpc = rpcRepository.findById(idRpc)
                .orElseThrow(() -> new IllegalArgumentException("RPC no encontrada"));

        if (cantidadRetornada < 0 || cantidadRetornada > rpc.getCantidadEntregada()) {
            throw new IllegalArgumentException("Cantidad retornada inválida.");
        }

        rpc.setCantidadRetornada(cantidadRetornada);
        rpc.setPendienteRetorno(rpc.getCantidadEntregada() > cantidadRetornada);

        return rpcRepository.save(rpc);
    }

    // ✅ Métodos de filtrado limpios (Delegamos el filtrado al Repository si es posible)
    public List<RpcControl> obtenerPendientes() {
        return rpcRepository.findByPendienteRetorno(true);
    }

    // ✅ Uso del Mapper para el Resumen
    public RpcResumenDTO obtenerResumen() {
        return rpcMapper.toResumenDTO(
                rpcRepository.count(),
                rpcRepository.countByPendienteRetorno(true),
                rpcRepository.countByPendienteRetorno(false),
                rpcRepository.sumCantidadEntregada(),
                rpcRepository.sumCantidadRetornada()
        );
    }
    public List<RpcControl> obtenerCompletados() {
        // Usamos el nuevo método del repositorio que corregimos antes
        return rpcRepository.findByPendienteRetorno(false);
    }

    @Transactional
    public RpcControl registrarDesdeRecepcionCedis(RecepcionCedis recepcion, RecepcionCedisDetalle detalle) {

        RpcControl rpc = new RpcControl();
        rpc.setNumeroCamion(recepcion.getNumeroCamion());
        rpc.setDepartamento(recepcion.getDepartamento());
        rpc.setTipoRpc(detalle.getTipoRpc());
        rpc.setCantidadEntregada(detalle.getCantidadRpc());
        rpc.setCantidadRetornada(0);
        rpc.setFechaRegistro(LocalDate.now());
        rpc.setPendienteRetorno(true);

        return rpcRepository.save(rpc);
    }
}