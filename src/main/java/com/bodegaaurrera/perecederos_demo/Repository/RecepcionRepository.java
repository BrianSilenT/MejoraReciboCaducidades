package com.bodegaaurrera.perecederos_demo.Repository;

import com.bodegaaurrera.perecederos_demo.Model.EstadoRecepcion;
import com.bodegaaurrera.perecederos_demo.Model.Producto;
import com.bodegaaurrera.perecederos_demo.Model.Recepcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecepcionRepository extends JpaRepository<Recepcion, Long> {
    // Buscar recepciones cuya caducidad sea antes de una fecha límite
    List<Recepcion> findByFechaCaducidadBefore(LocalDate limite);

    // Buscar recepciones por estado (ACEPTADA, RECHAZADA, PENDIENTE)
    List<Recepcion> findByEstado(EstadoRecepcion estado);

    Optional<Recepcion> findByProductoAndLoteAndFechaCaducidad(
            Producto producto,
            String lote,
            LocalDate fechaCaducidad
    );

}
