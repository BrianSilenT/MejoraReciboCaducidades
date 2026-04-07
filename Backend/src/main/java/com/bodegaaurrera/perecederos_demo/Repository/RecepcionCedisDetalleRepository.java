package com.bodegaaurrera.perecederos_demo.Repository;

import com.bodegaaurrera.perecederos_demo.Model.RecepcionCedisDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecepcionCedisDetalleRepository extends JpaRepository<RecepcionCedisDetalle, Long> {
    List<RecepcionCedisDetalle> findByRecepcionCedis_IdRecepcion(Long idRecepcion);
}