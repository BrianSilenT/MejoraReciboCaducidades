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

    public List<DiscrepanciaRecepcion> obtenerPorCamion(String numeroCamion) {
        return discrepanciaRepository.findByNumeroCamion(numeroCamion);
    }

    public List<DiscrepanciaRecepcion> obtenerPorDepartamento(String departamento) {
        return discrepanciaRepository.findByDepartamento(departamento);
    }

    public List<DiscrepanciaRecepcion> obtenerTodas() {
        return discrepanciaRepository.findAll();
    }
}
