package com.bodegaaurrera.perecederos_demo.Repository;

import com.bodegaaurrera.perecederos_demo.Enums.Departamento;
import com.bodegaaurrera.perecederos_demo.Enums.Division;
import com.bodegaaurrera.perecederos_demo.Model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    List<Inventario> findByFechaCaducidadBefore(LocalDate limite);

    @Query("SELECT i FROM Inventario i JOIN FETCH i.producto WHERE i.producto.codigoBarras = :codigo")
    List<Inventario> findDetalleCompleto(String codigo);

    List<Inventario> findByDivision(Division division);

    List<Inventario> findByDepartamento(Departamento departamento);


    List<Inventario> findByFechaCaducidadBetween(LocalDate inicio, LocalDate fin);

    // 🔹 Nuevo: contar caducados
    long countByFechaCaducidadBefore(LocalDate fecha);

    // 🔹 Nuevo: contar por caducar en rango
    long countByFechaCaducidadBetween(LocalDate inicio, LocalDate fin);

    // 🔹 Nuevo: sumar cantidades de inventario
    @Query("SELECT COALESCE(SUM(i.cantidad),0) FROM Inventario i")
    long sumCantidad();

}