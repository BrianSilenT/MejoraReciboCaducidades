package com.bodegaaurrera.perecederos_demo.Repository;

import com.bodegaaurrera.perecederos_demo.Model.RecepcionCedis;
import com.bodegaaurrera.perecederos_demo.Model.Departamento;
import com.bodegaaurrera.perecederos_demo.Model.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecepcionCedisRepository extends JpaRepository<RecepcionCedis, Long> {
    List<RecepcionCedis> findByNumeroCamion(String numeroCamion);
    List<RecepcionCedis> findByDepartamento(Departamento departamento);
    List<RecepcionCedis> findByDivision(Division division);
    List<RecepcionCedis> findByNumeroCamionAndDepartamentoIn(String numeroCamion, List<Departamento> departamentos);

    // ✅ Método correcto, sin static ni cuerpo
    @Query("SELECT DISTINCT r.numeroCamion FROM RecepcionCedis r WHERE r.estado = 'EN_DESEMBARQUE'")
    List<String> findCamionesDisponibles();

}