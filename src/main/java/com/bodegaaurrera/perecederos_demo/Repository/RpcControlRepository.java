package com.bodegaaurrera.perecederos_demo.Repository;

import com.bodegaaurrera.perecederos_demo.Model.RpcControl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RpcControlRepository extends JpaRepository<RpcControl, Long> {
    List<RpcControl> findByNumeroCamion(String numeroCamion);
    List<RpcControl> findByDepartamento(String departamento);
    List<RpcControl> findByPendienteRetorno(boolean pendienteRetorno);
}