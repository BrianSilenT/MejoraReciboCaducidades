package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.DTO.*;
import com.bodegaaurrera.perecederos_demo.Enums.Departamento;
import com.bodegaaurrera.perecederos_demo.Enums.EstadoRecepcion;
import com.bodegaaurrera.perecederos_demo.Model.*;
import com.bodegaaurrera.perecederos_demo.Repository.*;
import com.bodegaaurrera.perecederos_demo.mapper.RecepcionMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class RecepcionCedisService {

    private final RpcService rpcService;
    private final RecepcionCedisRepository recepcionCedisRepository;
    private final RecepcionCedisDetalleRepository recepcionCedisDetalleRepository;
    private final DiscrepanciaRecepcionRepository discrepanciaRepository;
    private final InventarioService inventarioService;
    private final RpcControlRepository rpcControlRepository;
    private final ProductoRepository productoRepository;
    private final RecepcionMapper recepcionMapper; // ✅ Inyectado


    private boolean aplicaRpc(RecepcionCedis recepcion, RecepcionCedisDetalle d) {
        return (recepcion.getDepartamento() == Departamento.FRUTAS
                || recepcion.getDepartamento() == Departamento.VERDURAS)
                && d.getCantidadRpc() != null
                && d.getCantidadRpc() > 0;
    }

    @Transactional
    public RecepcionCedis registrarRecepcionConDetalles(RecepcionCedisRequestDTO request) {

        int totalRecibido = request.getDetalles().stream()
                .mapToInt(RecepcionCedisDetalleDTO::getCantidadRecibida)
                .sum();

        if (totalRecibido > request.getTotalEsperado()) {
            throw new IllegalArgumentException("El total recibido no puede ser mayor al esperado");
        }

        RecepcionCedis recepcion = recepcionMapper.fromRequestToEntity(request, totalRecibido);
        RecepcionCedis saved = recepcionCedisRepository.save(recepcion);

        for (RecepcionCedisDetalleDTO d : request.getDetalles()) {

            validarDetalle(d, saved.getFechaRecepcion());

            RecepcionCedisDetalle detalle = new RecepcionCedisDetalle();
            detalle.setRecepcionCedis(saved);
            detalle.setProducto(productoRepository.findById(d.getIdProducto())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado")));
            detalle.setCantidadEsperada(d.getCantidadEsperada());
            detalle.setCantidadRecibida(d.getCantidadRecibida());
            detalle.setLote(d.getLote());
            detalle.setFechaCaducidad(d.getFechaCaducidad());
            detalle.setCantidadRpc(d.getCantidadRpc());
            detalle.setTipoRpc(d.getTipoRpc());

            recepcionCedisDetalleRepository.save(detalle);
        }

        return saved;
    }

    @Transactional
    public RecepcionAuditoriaDTO cerrarRecepcion(Long idRecepcion) {

        RecepcionCedis recepcion = recepcionCedisRepository.findById(idRecepcion)
                .orElseThrow(() -> new IllegalArgumentException("Recepción no encontrada"));

        // 🔒 BLOQUEO 1: evitar doble cierre
        if (recepcion.getEstado() == EstadoRecepcion.CERRADA) {
            throw new IllegalStateException("La recepción ya fue cerrada previamente");
        }

        if (recepcion.getEstado() == EstadoRecepcion.RECHAZADA) {
            throw new IllegalStateException("No se puede cerrar una recepción rechazada");
        }

        List<RecepcionCedisDetalle> detalles =
                recepcionCedisDetalleRepository.findByRecepcionCedis_IdRecepcion(idRecepcion);

        // 🔒 BLOQUEO 2: evitar cierre sin datos
        if (detalles.isEmpty()) {
            throw new IllegalArgumentException("No hay detalles para cerrar la recepción");
        }

        int totalRecibido = 0;

        for (RecepcionCedisDetalle d : detalles) {

            // 🔒 VALIDACIONES
            if (d.getCantidadRecibida() < 0) {
                throw new IllegalArgumentException("Cantidad inválida en producto " + d.getProducto().getIdProducto());
            }

            if (d.getCantidadEsperada() <= 0) {
                throw new IllegalArgumentException("Cantidad esperada inválida");
            }

            totalRecibido += d.getCantidadRecibida();

            // 🔥 INVENTARIO (solo si no estaba cerrada)
            if (d.getCantidadRecibida() > 0) {
                inventarioService.cargarInventarioDesdeRecepcionCedis(d, recepcion);
            }

            // 🔥 DISCREPANCIA (evitar duplicados)
            boolean yaExisteDiscrepancia = discrepanciaRepository
                    .findByNumeroCamion(recepcion.getNumeroCamion())
                    .stream()
                    .anyMatch(x ->
                            x.getTotalEsperado() == d.getCantidadEsperada() &&
                                    x.getTotalRecibido() == d.getCantidadRecibida()
                    );

            if (d.getCantidadRecibida() < d.getCantidadEsperada() && !yaExisteDiscrepancia) {
                registrarDiscrepancia(recepcion, d);
            }

            // 🔥 RPC (evitar duplicados)
            boolean yaExisteRpc = rpcControlRepository
                    .findByNumeroCamion(recepcion.getNumeroCamion())
                    .stream()
                    .anyMatch(r ->
                            r.getTipoRpc() == d.getTipoRpc()
                    );

            if (aplicaRpc(recepcion, d) && !yaExisteRpc) {
                rpcService.registrarDesdeRecepcionCedis(recepcion, d);
            }
        }

        // 🔥 TOTALES
        recepcion.setTotalRecibido(totalRecibido);

        if (recepcion.getTotalEsperado() == 0) {
            recepcion.setPorcentajeAuditado((double) 0);
        } else {
            recepcion.setPorcentajeAuditado((totalRecibido * 100.0) / recepcion.getTotalEsperado());
        }

        recepcion.setCompleta(totalRecibido == recepcion.getTotalEsperado());

        // 🔥 ESTADO INTELIGENTE
        if (totalRecibido == 0) {
            recepcion.setEstado(EstadoRecepcion.RECHAZADA);
        } else if (totalRecibido < recepcion.getTotalEsperado()) {
            recepcion.setEstado(EstadoRecepcion.PARCIAL);
        } else {
            recepcion.setEstado(EstadoRecepcion.CERRADA);
        }

        recepcionCedisRepository.save(recepcion);

        return construirRespuestaPlano(recepcion);
    }
    public RecepcionAuditoriaDTO construirRespuestaPlano(RecepcionCedis recepcion) {
        RecepcionAuditoriaDTO dto = new RecepcionAuditoriaDTO();
        dto.setRecepcion(recepcionMapper.toRecepcionDTO(recepcion));

        dto.setDetalles(recepcionCedisDetalleRepository
                .findByRecepcionCedis_IdRecepcion(recepcion.getIdRecepcion())
                .stream()
                .map(recepcionMapper::toDetallePlanoDTO) // Asumiendo que agregaste este al mapper
                .toList());

        dto.setDiscrepancias(recepcionMapper.toDiscrepanciaDTOs(
                discrepanciaRepository.findByNumeroCamion(recepcion.getNumeroCamion())));

        dto.setRpc(recepcionMapper.toRpcDTOs(
                rpcControlRepository.findByNumeroCamion(recepcion.getNumeroCamion())));

        return dto;
    }

    // --- Métodos de apoyo privados para mantener el Service limpio ---

    private void validarDetalle(RecepcionCedisDetalleDTO d, LocalDate fechaRecepcion) {
        if (d.getCantidadRecibida() > d.getCantidadEsperada())
            throw new IllegalArgumentException("Exceso en producto " + d.getIdProducto());
        if (d.getLote() == null || d.getLote().isBlank())
            throw new IllegalArgumentException("Lote obligatorio");
        if (d.getFechaCaducidad() == null || d.getFechaCaducidad().isBefore(fechaRecepcion))
            throw new IllegalArgumentException("Fecha caducidad inválida");
    }

    private void registrarDiscrepancia(RecepcionCedis recepcion, RecepcionCedisDetalle d) {

        DiscrepanciaRecepcion disc = new DiscrepanciaRecepcion();
        disc.setNumeroCamion(recepcion.getNumeroCamion());
        disc.setDepartamento(recepcion.getDepartamento().name());
        disc.setTotalEsperado(d.getCantidadEsperada());
        disc.setTotalRecibido(d.getCantidadRecibida());
        disc.setTotalFaltante(d.getCantidadEsperada() - d.getCantidadRecibida());
        disc.setFechaRegistro(LocalDate.now());

        discrepanciaRepository.save(disc);
    }

    public List<Map<String, Object>> listarCamiones() {
        return recepcionCedisRepository.findAll().stream()
                .map(r -> Map.<String, Object>of(
                        "numeroCamion", r.getNumeroCamion(),
                        "departamento", r.getDepartamento(),
                        "estado", r.getEstado()
                )).toList();
    }
    public RecepcionCedisResponseDTO construirRespuestaCompleta(List<RecepcionCedis> recepciones) {

        RecepcionCedisResponseDTO dto = new RecepcionCedisResponseDTO();

        RecepcionCedis principal = recepciones.get(0);

        dto.setRecepcion(recepcionMapper.toRecepcionDTO(principal));

        // 🔹 Unir todos los detalles de todas las recepciones del camión
        List<RecepcionCedisDetalle> detalles = recepciones.stream()
                .flatMap(r -> recepcionCedisDetalleRepository
                        .findByRecepcionCedis_IdRecepcion(r.getIdRecepcion())
                        .stream())
                .toList();

        dto.setDetalles(recepcionMapper.toDetalleDTOs(detalles));

        dto.setDiscrepancias(
                recepcionMapper.toDiscrepanciaDTOs(
                        discrepanciaRepository.findByNumeroCamion(principal.getNumeroCamion())
                )
        );

        dto.setRpc(
                recepcionMapper.toRpcDTOs(
                        rpcControlRepository.findByNumeroCamion(principal.getNumeroCamion())
                )
        );

        return dto;
    }

    public List<RecepcionCedis> obtenerPorCamion(String numeroCamion) {
        return recepcionCedisRepository.findByNumeroCamion(numeroCamion);
    }
    public RecepcionCedisResponseDTO obtenerPorCamionYDepartamento(String numeroCamion, String departamento) {

        Departamento depto = Departamento.valueOf(departamento.toUpperCase());

        List<RecepcionCedis> lista = recepcionCedisRepository
                .findByNumeroCamionAndDepartamento(numeroCamion, depto);

        if (lista.isEmpty()) {
            throw new IllegalArgumentException("No hay registros para ese camión y departamento");
        }

        return construirRespuestaCompleta(lista);
    }

}