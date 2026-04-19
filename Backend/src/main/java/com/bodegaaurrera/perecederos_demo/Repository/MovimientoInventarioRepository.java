package com.bodegaaurrera.perecederos_demo.Repository;

import com.bodegaaurrera.perecederos_demo.Enums.TipoMovimiento;
import com.bodegaaurrera.perecederos_demo.Model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    @Query("SELECT m FROM MovimientoInventario m JOIN FETCH m.producto WHERE m.producto.codigoBarras = :codigo")
    List<MovimientoInventario> findDetalleByCodigo(String codigo);List<MovimientoInventario> findByProductoCodigoBarras(String codigoBarras);

    List<MovimientoInventario> findByTipoMovimiento(TipoMovimiento tipoMovimiento);
}