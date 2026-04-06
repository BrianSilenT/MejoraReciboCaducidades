package com.bodegaaurrera.perecederos_demo.Controller;


import com.bodegaaurrera.perecederos_demo.Model.ApiResponse;
import com.bodegaaurrera.perecederos_demo.Model.Producto;
import com.bodegaaurrera.perecederos_demo.Service.ProductoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/producto")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    public ApiResponse<Producto> registrarProducto(@RequestBody Producto producto) {
        Producto nuevo = productoService.registrar(producto);
        return new ApiResponse<>(nuevo);
    }

    @GetMapping
    public ApiResponse<List<Producto>> listarProductos() {
        List<Producto> productos = productoService.listarTodos();
        return new ApiResponse<>(productos);
    }
}