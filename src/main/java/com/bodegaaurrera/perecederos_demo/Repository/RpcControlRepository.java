package com.bodegaaurrera.perecederos_demo.Repository;

import com.bodegaaurrera.perecederos_demo.Model.Departamento;
import com.bodegaaurrera.perecederos_demo.Model.RpcControl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RpcControlRepository extends JpaRepository<RpcControl, Long> {
    List<RpcControl> findByDepartamento(Departamento departamento);
    List<RpcControl> findByNumeroCamion(String numeroCamion);
}