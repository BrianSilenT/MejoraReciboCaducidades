package com.bodegaaurrera.perecederos_demo.Repository;

import com.bodegaaurrera.perecederos_demo.Model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByCodigoBarras(String codigoBarras);
    Optional<Producto> findByCodigoBarras(String codigoBarras);


}