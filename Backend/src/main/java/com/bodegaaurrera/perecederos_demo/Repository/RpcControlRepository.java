package com.bodegaaurrera.perecederos_demo.Repository;

import com.bodegaaurrera.perecederos_demo.Model.Departamento;
import com.bodegaaurrera.perecederos_demo.Model.RpcControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RpcControlRepository extends JpaRepository<RpcControl, Long> {
    List<RpcControl> findByDepartamento(Departamento departamento);
    List<RpcControl> findByNumeroCamion(String numeroCamion);

    // ✅ Contar pendientes
    long countByPendienteRetornoTrue();

    // ✅ Contar completados
    long countByPendienteRetornoFalse();

    // ✅ Sumar cantidad entregada
    @Query("SELECT COALESCE(SUM(r.cantidadEntregada), 0) FROM RpcControl r")
    Long sumCantidadEntregada();

    // ✅ Sumar cantidad retornada
    @Query("SELECT COALESCE(SUM(r.cantidadRetornada), 0) FROM RpcControl r")
    Long sumCantidadRetornada();

    Long countByPendienteRetorno(boolean b);
}