package com.bodegaaurrera.perecederos_demo.Repository;


import com.bodegaaurrera.perecederos_demo.Model.SugerenciaSurtido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SugerenciaSurtidoRepository extends JpaRepository<SugerenciaSurtido, Long> {
    List<SugerenciaSurtido> findByUpc(String upc);
}