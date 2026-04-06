package com.bodegaaurrera.perecederos_demo;

import com.bodegaaurrera.perecederos_demo.Model.Producto;
import com.bodegaaurrera.perecederos_demo.Repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    void guardarYBuscarProducto() {
        Producto producto = new Producto();
        producto.setNombre("Leche Lala");
        producto.setCategoria("Lácteos");
        producto.setPresentacion("Galón");
        producto.setProveedor("Lala");

        productoRepository.save(producto);

        assertThat(productoRepository.findAll())
                .extracting(Producto::getNombre)
                .contains("Leche Lala");
    }
}