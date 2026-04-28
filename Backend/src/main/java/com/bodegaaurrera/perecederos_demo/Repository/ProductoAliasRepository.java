package com.bodegaaurrera.perecederos_demo.Repository;

import com.bodegaaurrera.perecederos_demo.Model.ProductoAlias;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductoAliasRepository extends JpaRepository<ProductoAlias, Long> {

    Optional<ProductoAlias> findByCodigoVentaAndActivoTrue(String codigoVenta);
}