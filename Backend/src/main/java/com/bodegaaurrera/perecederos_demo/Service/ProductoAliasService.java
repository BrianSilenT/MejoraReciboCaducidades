package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.Model.ProductoAlias;
import com.bodegaaurrera.perecederos_demo.Repository.ProductoAliasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductoAliasService {

    private final ProductoAliasRepository repository;

    public String resolverUpcInventario(String upc) {

        return repository.findByCodigoVentaAndActivoTrue(upc)
                .map(ProductoAlias::getCodigoInventario)
                .orElse(upc);
    }
}