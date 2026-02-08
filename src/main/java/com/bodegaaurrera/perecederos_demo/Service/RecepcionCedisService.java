package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.Model.RecepcionCedis;
import com.bodegaaurrera.perecederos_demo.Model.DiscrepanciaRecepcion;
import com.bodegaaurrera.perecederos_demo.Repository.RecepcionCedisRepository;
import com.bodegaaurrera.perecederos_demo.Repository.DiscrepanciaRecepcionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RecepcionCedisService {

    private final RecepcionCedisRepository recepcionCedisRepository;
    private final DiscrepanciaRecepcionRepository discrepanciaRepository;

    public RecepcionCedisService(RecepcionCedisRepository recepcionCedisRepository,
                                 DiscrepanciaRecepcionRepository discrepanciaRepository) {
        this.recepcionCedisRepository = recepcionCedisRepository;
        this.discrepanciaRepository = discrepanciaRepository;
    }

    public RecepcionCedis registrarRecepcion(RecepcionCedis recepcion) {
        // Calcular porcentaje auditado
        double porcentaje = (double) recepcion.getTotalRecibido() / recepcion.getTotalEsperado() * 100;
        recepcion.setPorcentajeAuditado(porcentaje);
        recepcion.setFechaRecepcion(LocalDate.now());

        // Marcar completa si es 100%
        recepcion.setCompleta(porcentaje == 100);

        // Guardar recepción
        RecepcionCedis saved = recepcionCedisRepository.save(recepcion);

        // Si hay discrepancia, registrar
        if (porcentaje < 100) {
            DiscrepanciaRecepcion discrepancia = new DiscrepanciaRecepcion();
            discrepancia.setNumeroCamion(recepcion.getNumeroCamion());
            discrepancia.setDepartamento(recepcion.getDepartamento().name());
            discrepancia.setTotalEsperado(recepcion.getTotalEsperado());
            discrepancia.setTotalRecibido(recepcion.getTotalRecibido());
            discrepancia.setTotalFaltante(recepcion.getTotalEsperado() - recepcion.getTotalRecibido());
            discrepancia.setFechaRegistro(LocalDate.now());
            discrepanciaRepository.save(discrepancia);
        }

        return saved;
    }

    public List<RecepcionCedis> obtenerPorCamion(String numeroCamion) {
        return recepcionCedisRepository.findByNumeroCamion(numeroCamion);
    }

    public List<RecepcionCedis> obtenerPorDepartamento(String departamento) {
        return recepcionCedisRepository.findByDepartamento(
                Enum.valueOf(com.bodegaaurrera.perecederos_demo.Model.Departamento.class, departamento)
        );
    }

    public List<RecepcionCedis> obtenerPorDivision(String division) {
        return recepcionCedisRepository.findByDivision(
                Enum.valueOf(com.bodegaaurrera.perecederos_demo.Model.Division.class, division)
        );
    }
}
