package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.Model.DiscrepanciaRecepcion;
import com.bodegaaurrera.perecederos_demo.Repository.DiscrepanciaRecepcionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DiscrepanciaService {

    private final DiscrepanciaRecepcionRepository discrepanciaRepository;

    public DiscrepanciaService(DiscrepanciaRecepcionRepository discrepanciaRepository) {
        this.discrepanciaRepository = discrepanciaRepository;
    }

    // ✅ Registrar discrepancia
    public DiscrepanciaRecepcion registrarDiscrepancia(DiscrepanciaRecepcion discrepancia) {
        if (discrepancia.getTotalEsperado() <= 0) {
            throw new IllegalArgumentException("El total esperado debe ser mayor a 0.");
        }
        if (discrepancia.getTotalRecibido() < 0) {
            throw new IllegalArgumentException("El total recibido no puede ser negativo.");
        }
        if (discrepancia.getTotalFaltante() < 0) {
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
