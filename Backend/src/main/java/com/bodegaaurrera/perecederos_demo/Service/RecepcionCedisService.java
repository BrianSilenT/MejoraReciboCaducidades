package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.DTO.*;
import com.bodegaaurrera.perecederos_demo.Enums.*;
import com.bodegaaurrera.perecederos_demo.Model.*;
import com.bodegaaurrera.perecederos_demo.Repository.*;
import com.bodegaaurrera.perecederos_demo.mapper.RecepcionMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecepcionCedisService {

    private final RpcService rpcService;
    private final RecepcionCedisRepository recepcionCedisRepository;
    private final RecepcionCedisDetalleRepository detalleRepository;
    private final DiscrepanciaRecepcionRepository discrepanciaRepository;
    private final RpcControlRepository rpcControlRepository;
    private final ProductoRepository productoRepository;
    private final RecepcionMapper recepcionMapper;
    private final MovimientoInventarioService movimientoService;

    // =========================
    // REGISTRO
    // =========================
    @Transactional
    public RecepcionCedis registrarRecepcionConDetalles(RecepcionCedisRequestDTO request) {

        BigDecimal totalRecibido = request.getDetalles().stream()
                .map(RecepcionCedisDetalleDTO::getCantidadRecibida)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalRecibido.compareTo(request.getTotalEsperado()) > 0) {
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

            detalleRepository.save(detalle);
        }

        return saved;
    }

    // =========================
    // CIERRE (IMPACTA INVENTARIO)
    // =========================
    @Transactional
    public RecepcionAuditoriaDTO cerrarRecepcion(Long idRecepcion) {

        RecepcionCedis recepcion = recepcionCedisRepository.findById(idRecepcion)
                .orElseThrow(() -> new IllegalArgumentException("Recepción no encontrada"));

        if (recepcion.getEstado() == EstadoRecepcion.CERRADA) {
            throw new IllegalStateException("Ya fue cerrada");
        }

        List<RecepcionCedisDetalle> detalles =
                detalleRepository.findByRecepcionCedis_IdRecepcion(idRecepcion);

        if (detalles.isEmpty()) {
            throw new IllegalArgumentException("Sin detalles");
        }

        BigDecimal totalRecibido = BigDecimal.ZERO;

        for (RecepcionCedisDetalle d : detalles) {

            if (d.getCantidadRecibida().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Cantidad inválida");
            }

            totalRecibido = totalRecibido.add(d.getCantidadRecibida());

            // 🔥 INVENTARIO
            if (d.getCantidadRecibida().compareTo(BigDecimal.ZERO) > 0) {
                movimientoService.ejecutarMovimiento(
                        d.getProducto().getCodigoBarras(),
                        d.getLote(),
                        d.getCantidadRecibida(),
                        TipoMovimiento.RECEPCION,
                        null,
                        Ubicacion.BODEGA,
                        recepcion.getNumeroCamion(),
                        "Recepción CEDIS"
                );
            }

            // 🔥 DISCREPANCIA
            if (d.getCantidadRecibida().compareTo(d.getCantidadEsperada()) < 0) {
                registrarDiscrepancia(recepcion, d);
            }

            // 🔥 RPC
            if (aplicaRpc(recepcion, d)) {
                rpcService.registrarDesdeRecepcionCedis(recepcion, d);
            }
        }

        recepcion.setTotalRecibido(totalRecibido);

        if (recepcion.getTotalEsperado().compareTo(BigDecimal.ZERO) == 0) {

            recepcion.setPorcentajeAuditado(0.0);

        } else {

            recepcion.setPorcentajeAuditado(
                    totalRecibido
                            .multiply(BigDecimal.valueOf(100))
                            .divide(recepcion.getTotalEsperado(), 2, RoundingMode.HALF_UP)
                            .doubleValue()
            );
        }

        recepcion.setCompleta(
                totalRecibido.compareTo(recepcion.getTotalEsperado()) == 0
        );

        // 🔥 ESTADO
        if (totalRecibido.compareTo(BigDecimal.ZERO) == 0) {
            recepcion.setEstado(EstadoRecepcion.RECHAZADA);
        } else if (totalRecibido.compareTo(recepcion.getTotalEsperado()) < 0) {
            recepcion.setEstado(EstadoRecepcion.PARCIAL);
        } else {
            recepcion.setEstado(EstadoRecepcion.CERRADA);
        }

        recepcionCedisRepository.save(recepcion);

        return construirRespuestaPlano(recepcion);
    }

    // =========================
    // RESPUESTA AUDITORIA
    // =========================
    public RecepcionAuditoriaDTO construirRespuestaPlano(RecepcionCedis recepcion) {

        RecepcionAuditoriaDTO dto = new RecepcionAuditoriaDTO();

        dto.setRecepcion(recepcionMapper.toRecepcionDTO(recepcion));

        dto.setDetalles(
                detalleRepository.findByRecepcionCedis_IdRecepcion(recepcion.getIdRecepcion())
                        .stream()
                        .map(recepcionMapper::toDetallePlanoDTO)
                        .toList()
        );

        dto.setDiscrepancias(
                recepcionMapper.toDiscrepanciaDTOs(
                        discrepanciaRepository.findByNumeroCamion(recepcion.getNumeroCamion())
                )
        );

        dto.setRpc(
                recepcionMapper.toRpcDTOs(
                        rpcControlRepository.findByNumeroCamion(recepcion.getNumeroCamion())
                )
        );

        return dto;
    }

    // =========================
    // CONTROLLER SUPPORT
    // =========================

    public List<RecepcionCedis> obtenerPorCamion(String numeroCamion) {
        return recepcionCedisRepository.findByNumeroCamion(numeroCamion);
    }

    public RecepcionCedisResponseDTO obtenerPorCamionYDepartamento(String numeroCamion, String departamento) {

        Departamento depto = Departamento.valueOf(departamento.toUpperCase());

        List<RecepcionCedis> lista =
                recepcionCedisRepository.findByNumeroCamionAndDepartamento(numeroCamion, depto);

        if (lista.isEmpty()) {
            throw new IllegalArgumentException("Sin registros");
        }

        return construirRespuestaCompleta(lista);
    }

    public RecepcionCedisResponseDTO construirRespuestaCompleta(List<RecepcionCedis> recepciones) {

        RecepcionCedis principal = recepciones.get(0);

        RecepcionCedisResponseDTO dto = new RecepcionCedisResponseDTO();
        dto.setRecepcion(recepcionMapper.toRecepcionDTO(principal));

        List<RecepcionCedisDetalle> detalles = recepciones.stream()
                .flatMap(r -> detalleRepository
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

    public List<Map<String, Object>> listarCamiones() {
        return recepcionCedisRepository.findAll().stream()
                .map(r -> Map.<String, Object>of(
                        "numeroCamion", r.getNumeroCamion(),
                        "departamento", r.getDepartamento(),
                        "estado", r.getEstado()
                ))
                .toList();
    }

    // =========================
    // HELPERS
    // =========================

    private void validarDetalle(RecepcionCedisDetalleDTO d, LocalDate fecha) {

        if (d.getCantidadRecibida().compareTo(d.getCantidadEsperada()) > 0) {
            throw new IllegalArgumentException("Excede esperado");
        }

        if (d.getLote() == null || d.getLote().isBlank()) {
            throw new IllegalArgumentException("Lote obligatorio");
        }

        if (d.getFechaCaducidad() == null || d.getFechaCaducidad().isBefore(fecha)) {
            throw new IllegalArgumentException("Caducidad inválida");
        }
    }

    private void registrarDiscrepancia(RecepcionCedis r, RecepcionCedisDetalle d) {

        DiscrepanciaRecepcion disc = new DiscrepanciaRecepcion();

        disc.setNumeroCamion(r.getNumeroCamion());
        disc.setDepartamento(r.getDepartamento().name());
        disc.setTotalEsperado(d.getCantidadEsperada());
        disc.setTotalRecibido(d.getCantidadRecibida());
        disc.setTotalFaltante(
                d.getCantidadEsperada().subtract(d.getCantidadRecibida())
        );
        disc.setFechaRegistro(LocalDate.now());

        discrepanciaRepository.save(disc);
    }

    private boolean aplicaRpc(RecepcionCedis r, RecepcionCedisDetalle d) {
        return (r.getDepartamento() == Departamento.FRUTAS
                || r.getDepartamento() == Departamento.VERDURAS)
                && d.getCantidadRpc() != null
                && d.getCantidadRpc() > 0;
    }
}