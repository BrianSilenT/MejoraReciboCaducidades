package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.Model.Producto;
import com.bodegaaurrera.perecederos_demo.Repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductoService {
    private final ProductoRepository productoRepository;


    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }


    public Producto registrar(Producto producto) {
        if (productoRepository.existsByCodigoBarras(producto.getCodigoBarras())) {
            throw new RuntimeException("El código de barras ya existe");
        }
        return productoRepository.save(producto);
    }

}