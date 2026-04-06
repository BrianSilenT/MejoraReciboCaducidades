package com.bodegaaurrera.perecederos_demo.Repository;

import com.bodegaaurrera.perecederos_demo.Model.Departamento;
import com.bodegaaurrera.perecederos_demo.Model.Division;
import com.bodegaaurrera.perecederos_demo.Model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    List<Inventario> findByFechaCaducidadBefore(LocalDate limite);
    List<Inventario> findByCodigoBarras(String codigoBarras);
    List<Inventario> findByDivision(Division division);
    List<Inventario> findByDepartamento(Departamento departamento);
    Optional<Inventario> findByCodigoBarrasAndLote(String codigoBarras, String lote);

    // 🔹 Nuevo: contar caducados
    long countByFechaCaducidadBefore(LocalDate fecha);

    // 🔹 Nuevo: contar por caducar en rango
    long countByFechaCaducidadBetween(LocalDate inicio, LocalDate fin);

    // 🔹 Nuevo: sumar cantidades de inventario
    @Query("SELECT COALESCE(SUM(i.cantidad),0) FROM Inventario i")
    long sumCantidad();
}