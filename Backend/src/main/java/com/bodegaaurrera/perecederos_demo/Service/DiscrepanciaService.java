package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.Model.DiscrepanciaRecepcion;
import com.bodegaaurrera.perecederos_demo.Repository.DiscrepanciaRecepcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DiscrepanciaService {

    private final DiscrepanciaRecepcionRepository discrepanciaRepository;


    public DiscrepanciaRecepcion registrarDiscrepancia(DiscrepanciaRecepcion discrepancia) {

        if (discrepancia.getTotalEsperado() == null ||
                discrepancia.getTotalEsperado().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException("El total esperado debe ser mayor a 0.");
        }

        if (discrepancia.getTotalRecibido() == null ||
                discrepancia.getTotalRecibido().compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException("El total recibido no puede ser negativo.");
        }

        if (discrepancia.getTotalFaltante() == null ||
                discrepancia.getTotalFaltante().compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException("El total faltante no puede ser negativo.");
        }

        return discrepanciaRepository.save(discrepancia);
    }

    public List<DiscrepanciaRecepcion> listarTodas() {
        return discrepanciaRepository.findAll();
    }

    public List<DiscrepanciaRecepcion> obtenerPorCamion(String numeroCamion) {
        return discrepanciaRepository.findByNumeroCamion(numeroCamion);
    }

    public List<DiscrepanciaRecepcion> obtenerPorDepartamento(String departamento) {
        return discrepanciaRepository.findByDepartamento(departamento);
    }
}
