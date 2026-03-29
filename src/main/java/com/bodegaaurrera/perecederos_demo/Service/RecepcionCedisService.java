package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.DTO.*;
import com.bodegaaurrera.perecederos_demo.Model.*;
import com.bodegaaurrera.perecederos_demo.Repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class RecepcionCedisService {

    private final RecepcionCedisRepository recepcionCedisRepository;
    private final RecepcionCedisDetalleRepository recepcionCedisDetalleRepository;
    private final DiscrepanciaRecepcionRepository discrepanciaRepository;
    private final InventarioService inventarioService;
    private final RpcControlRepository rpcControlRepository;
    private final ProductoRepository productoRepository;

    public RecepcionCedisService(RecepcionCedisRepository recepcionCedisRepository,
                                 RecepcionCedisDetalleRepository recepcionCedisDetalleRepository,
                                 DiscrepanciaRecepcionRepository discrepanciaRepository,
                                 InventarioService inventarioService,
                                 RpcControlRepository rpcControlRepository,
                                 ProductoRepository productoRepository) {
        this.recepcionCedisRepository = recepcionCedisRepository;
        this.recepcionCedisDetalleRepository = recepcionCedisDetalleRepository;
        this.discrepanciaRepository = discrepanciaRepository;
        this.inventarioService = inventarioService;
        this.rpcControlRepository = rpcControlRepository;
        this.productoRepository = productoRepository;
    }

    public RecepcionCedisResponseDTO obtenerPorDepartamentoConDTO(String departamento) {
        List<RecepcionCedis> recepciones = recepcionCedisRepository.findByDepartamento(
                Enum.valueOf(Departamento.class, departamento)
        );
        if (recepciones.isEmpty()) {
            return null;
        }
        return construirRespuesta(recepciones.get(0));
    }


    public RecepcionCedis registrarRecepcionConDetalles(RecepcionCedisRequestDTO request) {
        // Calcular total recibido
        int totalRecibido = request.getDetalles().stream()
                .mapToInt(RecepcionCedisDetalleDTO::getCantidadRecibida)
                .sum();

        if (totalRecibido > request.getTotalEsperado()) {
            throw new IllegalArgumentException("El total recibido no puede ser mayor al esperado");
        }

        // Crear cabecera de recepción
        RecepcionCedis recepcion = new RecepcionCedis();
        recepcion.setNumeroCamion(request.getNumeroCamion());
        recepcion.setDepartamento(Departamento.valueOf(request.getDepartamento().toUpperCase()));
        recepcion.setDivision(Division.valueOf(request.getDivision().toUpperCase()));
        recepcion.setTotalEsperado(request.getTotalEsperado());
        recepcion.setFechaRecepcion(LocalDate.now());

        recepcion.setTotalRecibido(totalRecibido);
        recepcion.setPorcentajeAuditado((totalRecibido * 100.0) / recepcion.getTotalEsperado());
        recepcion.setCompleta(totalRecibido == recepcion.getTotalEsperado());
        recepcion.setEstado(totalRecibido == recepcion.getTotalEsperado() ? EstadoRecepcion.ACEPTADA : EstadoRecepcion.PARCIAL);

        RecepcionCedis saved = recepcionCedisRepository.save(recepcion);

        // Procesar detalles
        for (RecepcionCedisDetalleDTO d : request.getDetalles()) {
            // Validaciones obligatorias
            if (d.getCantidadRecibida() > d.getCantidadEsperada()) {
                throw new IllegalArgumentException("Cantidad recibida excede lo esperado para el producto " + d.getIdProducto());
            }
            if (d.getLote() == null || d.getLote().isBlank()) {
                throw new IllegalArgumentException("El lote es obligatorio");
            }
            if (d.getFechaCaducidad() == null || d.getFechaCaducidad().isBefore(recepcion.getFechaRecepcion())) {
                throw new IllegalArgumentException("La fecha de caducidad debe ser posterior a la recepción");
            }

            RecepcionCedisDetalle detalle = new RecepcionCedisDetalle();
            detalle.setRecepcionCedis(saved);
            detalle.setProducto(productoRepository.findById(d.getIdProducto())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado")));
            detalle.setCantidadEsperada(d.getCantidadEsperada());   // 🔹 nuevo
            detalle.setCantidadRecibida(d.getCantidadRecibida());
            detalle.setLote(d.getLote());
            detalle.setFechaCaducidad(d.getFechaCaducidad());
            detalle.setCantidadRpc(d.getCantidadRpc());
            recepcionCedisDetalleRepository.save(detalle);

            // Inventario: solo si cantidad recibida > 0
            if (d.getCantidadRecibida() > 0) {
                Inventario inv = new Inventario();
                inv.setCodigoBarras(detalle.getProducto().getCodigoBarras());
                inv.setDescripcion(detalle.getProducto().getDescripcion());
                inv.setCantidad(detalle.getCantidadRecibida());
                inv.setLote(detalle.getLote());
                inv.setFechaCaducidad(detalle.getFechaCaducidad());
                inv.setFechaLlegada(LocalDate.now());
                inv.setDivision(saved.getDivision());
                inv.setDepartamento(saved.getDepartamento());
                inventarioService.registrar(inv);
            }

            // Discrepancia: si recibido < esperado
            if (d.getCantidadRecibida() < d.getCantidadEsperada()) {
                DiscrepanciaRecepcion discrepancia = new DiscrepanciaRecepcion();
                discrepancia.setNumeroCamion(saved.getNumeroCamion());
                discrepancia.setDepartamento(saved.getDepartamento().name());
                discrepancia.setTotalEsperado(d.getCantidadEsperada());
                discrepancia.setTotalRecibido(d.getCantidadRecibida());
                discrepancia.setTotalFaltante(d.getCantidadEsperada() - d.getCantidadRecibida());
                discrepancia.setFechaRegistro(LocalDate.now());
                discrepanciaRepository.save(discrepancia);
            }

            // RPC: solo frutas/verduras y si cantidadRpc > 0
            if ((saved.getDepartamento() == Departamento.FRUTAS || saved.getDepartamento() == Departamento.VERDURAS)
                    && d.getCantidadRpc() != null && d.getCantidadRpc() > 0) {
                RpcControl rpc = new RpcControl();
                rpc.setNumeroCamion(saved.getNumeroCamion());
                rpc.setDepartamento(saved.getDepartamento());
                rpc.setTipoRpc(d.getTipoRpc()); // usar enum en DTO
                rpc.setCantidadEntregada(d.getCantidadRpc());
                rpc.setCantidadRetornada(0);
                rpc.setFechaRegistro(LocalDate.now());
                rpc.setPendienteRetorno(true);
                rpcControlRepository.save(rpc);
            }
        }

        return saved;
    }

    public RecepcionCedisResponseDTO construirRespuesta(RecepcionCedis recepcion) {
        RecepcionCedisResponseDTO dto = new RecepcionCedisResponseDTO();
        dto.setRecepcion(recepcion);
        dto.setDetalles(recepcionCedisDetalleRepository.findByRecepcionCedis_IdRecepcionCedis(recepcion.getIdRecepcionCedis()));
        dto.setDiscrepancias(discrepanciaRepository.findByNumeroCamion(recepcion.getNumeroCamion()));
        dto.setRpc(rpcControlRepository.findByNumeroCamion(recepcion.getNumeroCamion()));
        return dto;
    }

    public List<RecepcionCedis> obtenerPorCamion(String numeroCamion) {
        return recepcionCedisRepository.findByNumeroCamion(numeroCamion);
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

    public List<RecepcionCedis> obtenerPorDivision(String division) {
        return recepcionCedisRepository.findByDivision(
                Enum.valueOf(Division.class, division)
        );
    }

    public RecepcionCedisResponseDTO obtenerFrutasYVerduras(String numeroCamion) {
        List<RecepcionCedis> recepciones = recepcionCedisRepository.findByNumeroCamionAndDepartamentoIn(
                numeroCamion, List.of(Departamento.FRUTAS, Departamento.VERDURAS)
        );
        if (recepciones.isEmpty()) {
            return null;
        }
        RecepcionCedisResponseDTO dto = construirRespuesta(recepciones.get(0));
        recepciones.stream().skip(1).forEach(r -> {
            dto.getDetalles().addAll(
                    recepcionCedisDetalleRepository.findByRecepcionCedis_IdRecepcionCedis(r.getIdRecepcionCedis())
            );
        });
        return dto;
    }


    public RecepcionAuditoriaDTO construirRespuestaPlano(RecepcionCedis recepcion) {
        RecepcionAuditoriaDTO dto = new RecepcionAuditoriaDTO();

        // Detalles simplificados
        List<DetallePlanoDTO> detalles = recepcionCedisDetalleRepository
                .findByRecepcionCedis_IdRecepcionCedis(recepcion.getIdRecepcionCedis())
                .stream()
                .map(d -> {
                    DetallePlanoDTO det = new DetallePlanoDTO();
                    det.setIdDetalleCedis(d.getIdDetalleCedis());
                    det.setProducto(d.getProducto().getNombre());
                    det.setCantidadRecibida(d.getCantidadRecibida());
                    det.setLote(d.getLote());
                    det.setFechaCaducidad(d.getFechaCaducidad());
                    det.setCantidadRpc(d.getCantidadRpc());
                    return det;
                })
                .toList();
        dto.setDetalles(detalles);

        // Discrepancias → mapear Model → DTO
        List<DiscrepanciaDTO> discrepancias = discrepanciaRepository.findByNumeroCamion(recepcion.getNumeroCamion())
                .stream()
                .map(d -> {
                    DiscrepanciaDTO disc = new DiscrepanciaDTO();
                    disc.setIdDiscrepancia(d.getIdDiscrepancia());
                    disc.setTotalFaltante(d.getTotalFaltante());
                    return disc;
                })
                .toList();
        dto.setDiscrepancias(discrepancias);

        // RPC → mapear Model → DTO
        List<RpcDTO> rpcList = rpcControlRepository.findByNumeroCamion(recepcion.getNumeroCamion())
                .stream()
                .map(r -> {
                    RpcDTO rpc = new RpcDTO();
                    rpc.setIdRpc(r.getIdRpc());
                    rpc.setTipoRpc(r.getTipoRpc().name());
                    rpc.setCantidadEntregada(r.getCantidadEntregada());
                    rpc.setCantidadRetornada(r.getCantidadRetornada());
                    rpc.setPendienteRetorno(r.isPendienteRetorno());
                    rpc.setFechaRegistro(r.getFechaRegistro());
                    return rpc;
                })
                .toList();
        dto.setRpc(rpcList);

        return dto;
    }

    public RecepcionAuditoriaDTO cerrarRecepcion(Long idRecepcion) {
        // Buscar la recepción
        RecepcionCedis recepcion = recepcionCedisRepository.findById(idRecepcion)
                .orElseThrow(() -> new IllegalArgumentException("Recepción no encontrada"));

        // Recalcular totales
        int totalRecibido = recepcionCedisDetalleRepository
                .findByRecepcionCedis_IdRecepcionCedis(idRecepcion)
                .stream()
                .mapToInt(RecepcionCedisDetalle::getCantidadRecibida)
                .sum();

        recepcion.setTotalRecibido(totalRecibido);
        recepcion.setPorcentajeAuditado((totalRecibido * 100.0) / recepcion.getTotalEsperado());
        recepcion.setCompleta(totalRecibido == recepcion.getTotalEsperado());

        recepcionCedisRepository.save(recepcion);

        // Registrar discrepancia si no está completa
        if (!recepcion.isCompleta()) {
            DiscrepanciaRecepcion discrepancia = new DiscrepanciaRecepcion();
            discrepancia.setNumeroCamion(recepcion.getNumeroCamion());
            discrepancia.setDepartamento(recepcion.getDepartamento().name());
            discrepancia.setTotalEsperado(recepcion.getTotalEsperado());
            discrepancia.setTotalRecibido(recepcion.getTotalRecibido());
            discrepancia.setTotalFaltante(recepcion.getTotalEsperado() - recepcion.getTotalRecibido());
            discrepancia.setFechaRegistro(LocalDate.now());
            discrepanciaRepository.save(discrepancia);
        }

        // Construir respuesta de auditoría
        return construirRespuestaPlano(recepcion);
    }

    public RecepcionCedisResponseDTO construirRespuestaCompleta(List<RecepcionCedis> recepciones) {
        RecepcionCedisResponseDTO dto = new RecepcionCedisResponseDTO();

        // Cabecera combinada
        RecepcionCedis cabecera = new RecepcionCedis();
        cabecera.setNumeroCamion(recepciones.get(0).getNumeroCamion());
        cabecera.setDivision(recepciones.get(0).getDivision());
        cabecera.setFechaRecepcion(LocalDate.now());
        cabecera.setDepartamento(null); // o "MULTIPLE"

        int totalEsperado = recepciones.stream().mapToInt(RecepcionCedis::getTotalEsperado).sum();
        int totalRecibido = recepciones.stream().mapToInt(RecepcionCedis::getTotalRecibido).sum();

        cabecera.setTotalEsperado(totalEsperado);
        cabecera.setTotalRecibido(totalRecibido);
        cabecera.setPorcentajeAuditado((totalRecibido * 100.0) / totalEsperado);
        cabecera.setCompleta(totalRecibido == totalEsperado);
        cabecera.setEstado(totalRecibido == totalEsperado ? EstadoRecepcion.ACEPTADA : EstadoRecepcion.PARCIAL);

        dto.setRecepcion(cabecera);

        // Unir todos los detalles
        List<RecepcionCedisDetalle> todosDetalles = recepciones.stream()
                .flatMap(r -> recepcionCedisDetalleRepository
                        .findByRecepcionCedis_IdRecepcionCedis(r.getIdRecepcionCedis())
                        .stream())
                .toList();
        dto.setDetalles(todosDetalles);

        // Discrepancias y RPC del camión
        dto.setDiscrepancias(discrepanciaRepository.findByNumeroCamion(cabecera.getNumeroCamion()));
        dto.setRpc(rpcControlRepository.findByNumeroCamion(cabecera.getNumeroCamion()));

        return dto;
    }

}