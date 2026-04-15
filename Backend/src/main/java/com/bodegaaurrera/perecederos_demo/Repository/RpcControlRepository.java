package com.bodegaaurrera.perecederos_demo.Repository;

import com.bodegaaurrera.perecederos_demo.Enums.Departamento;
import com.bodegaaurrera.perecederos_demo.Model.RpcControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface RpcControlRepository extends JpaRepository<RpcControl, Long> {

    List<RpcControl> findByDepartamento(Departamento departamento);

    List<RpcControl> findByNumeroCamion(String numeroCamion);

    List<RpcControl> findByPendienteRetorno(boolean pendiente);

    // ✅ Usamos la versión genérica para que el Service pase true/false
    long countByPendienteRetorno(boolean b);

    // ✅ COALESCE es vital para evitar NullPointerException si la tabla está vacía
    @Query("SELECT COALESCE(SUM(r.cantidadEntregada), 0) FROM RpcControl r")
    long sumCantidadEntregada();

    @Query("SELECT COALESCE(SUM(r.cantidadRetornada), 0) FROM RpcControl r")
    long sumCantidadRetornada();

}