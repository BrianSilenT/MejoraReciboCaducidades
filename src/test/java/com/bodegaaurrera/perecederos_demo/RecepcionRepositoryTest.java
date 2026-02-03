package com.bodegaaurrera.perecederos_demo;

import com.bodegaaurrera.perecederos_demo.Model.Producto;
import com.bodegaaurrera.perecederos_demo.Model.Recepcion;
import com.bodegaaurrera.perecederos_demo.Repository.ProductoRepository;
import com.bodegaaurrera.perecederos_demo.Repository.RecepcionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RecepcionRepositoryTest {

    @Autowired
    private RecepcionRepository recepcionRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    void encontrarProductosProximosACaducar() {
        Producto producto = new Producto();
        producto.setNombre("Jamón Tradicional");
        producto.setCategoria("Embutidos");
        producto.setPresentacion("Paquete");
        producto.setProveedor("Lala");
        productoRepository.save(producto);

        Recepcion recepcion = new Recepcion();
        recepcion.setProducto(producto);
        recepcion.setCantidad(20);
        recepcion.setFechaRecepcion(LocalDate.now());
        recepcion.setFechaCaducidad(LocalDate.now().plusDays(5)); // caduca en 5 días
        recepcion.setLote("J001");
        recepcionRepository.save(recepcion);

        List<Recepcion> proximos = recepcionRepository.findByFechaCaducidadBefore(LocalDate.now().plusDays(7));

        assertThat(proximos).isNotEmpty();
        assertThat(proximos.get(0).getProducto().getNombre()).isEqualTo("Jamón Tradicional");
    }
}