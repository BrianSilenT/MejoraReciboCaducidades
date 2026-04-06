package com.bodegaaurrera.perecederos_demo.Repository;

import com.bodegaaurrera.perecederos_demo.Model.DiscrepanciaRecepcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscrepanciaRecepcionRepository extends JpaRepository<DiscrepanciaRecepcion, Long> {
    List<DiscrepanciaRecepcion> findByNumeroCamion(String numeroCamion);
    List<DiscrepanciaRecepcion> findByDepartamento(String departamento);
}
