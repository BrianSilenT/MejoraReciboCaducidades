package com.bodegaaurrera.perecederos_demo.Repository;

import com.bodegaaurrera.perecederos_demo.Model.Departamento;
import com.bodegaaurrera.perecederos_demo.Model.Division;
import com.bodegaaurrera.perecederos_demo.Model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    List<Inventario> findByFechaCaducidadBefore(LocalDate limite);
    Optional<Inventario> findByCodigoBarras(String codigoBarras);
    List<Inventario> findByDivision(Division division);
    List<Inventario> findByDepartamento(Departamento departamento);
    Optional<Inventario> findByCodigoBarrasAndLote(String codigoBarras, String lote);
}