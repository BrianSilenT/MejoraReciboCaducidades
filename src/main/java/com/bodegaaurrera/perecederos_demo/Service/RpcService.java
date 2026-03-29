package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.Model.RpcControl;
import com.bodegaaurrera.perecederos_demo.Model.Departamento;
import com.bodegaaurrera.perecederos_demo.Repository.RpcControlRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RpcService {

    private final RpcControlRepository rpcRepository;

    public RpcService(RpcControlRepository rpcRepository) {
        this.rpcRepository = rpcRepository;
    }

    // ✅ Registrar entrega
    public RpcControl registrarEntrega(RpcControl rpc) {
        if (rpc.getCantidadEntregada() <= 0) {
            throw new IllegalArgumentException("La cantidad entregada debe ser mayor a 0.");
        }

        rpc.setDepartamento(Departamento.FRUTAS); // 🔹 ahora enum
        rpc.setFechaRegistro(LocalDate.now());
        rpc.setPendienteRetorno(true);

        return rpcRepository.save(rpc);
    }

    // ✅ Registrar retorno
    public RpcControl registrarRetorno(Long idRpc, int cantidadRetornada) {
        RpcControl rpc = rpcRepository.findById(idRpc)
                .orElseThrow(() -> new IllegalArgumentException("RPC no encontrada con id: " + idRpc));

        if (cantidadRetornada < 0 || cantidadRetornada > rpc.getCantidadEntregada()) {
            throw new IllegalArgumentException("Cantidad retornada inválida.");
        }

        rpc.setCantidadRetornada(cantidadRetornada);
        rpc.setPendienteRetorno(rpc.getCantidadEntregada() > cantidadRetornada);

        return rpcRepository.save(rpc);
    }

    // ✅ Pendientes en frutas
    public List<RpcControl> obtenerPendientes() {
        return rpcRepository.findByDepartamento(Departamento.FRUTAS).stream()
                .filter(RpcControl::isPendienteRetorno)
                .toList();
    }

    // ✅ Completados en frutas
    public List<RpcControl> obtenerCompletados() {
        return rpcRepository.findByDepartamento(Departamento.FRUTAS).stream()
                .filter(rpc -> !rpc.isPendienteRetorno())
                .toList();
    }

    // ✅ RPC por camión en frutas
    public List<RpcControl> obtenerPorCamion(String numeroCamion) {
        return rpcRepository.findByNumeroCamion(numeroCamion).stream()
                .filter(rpc -> rpc.getDepartamento() == Departamento.FRUTAS)
                .toList();
    }

    // ✅ Listar todas las RPC
    public List<RpcControl> listarTodas() {
        return rpcRepository.findAll();
    }

    public Map<String, Object> obtenerResumen() {
        Map<String, Object> resumen = new HashMap<>();

        Long totalRegistradas = rpcRepository.count();
        Long totalPendientes = rpcRepository.countByPendienteRetorno(true);
        Long totalCompletadas = rpcRepository.countByPendienteRetorno(false);
        Long totalEntregado = rpcRepository.sumCantidadEntregada();
        Long totalRetornado = rpcRepository.sumCantidadRetornada();
        Long totalFaltante = totalEntregado - totalRetornado;

        resumen.put("total_registradas", totalRegistradas);
        resumen.put("total_pendientes", totalPendientes);
        resumen.put("total_completadas", totalCompletadas);
        resumen.put("total_entregado", totalEntregado);
        resumen.put("total_retornado", totalRetornado);
        resumen.put("total_faltante", totalFaltante);

        return resumen;
    }
}