package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.DTO.*;
import com.bodegaaurrera.perecederos_demo.Model.*;
import com.bodegaaurrera.perecederos_demo.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Transactional
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

    private RecepcionCedisDTO mapToRecepcionDTO(RecepcionCedis entity) {
        RecepcionCedisDTO dto = new RecepcionCedisDTO();
        dto.setIdRecepcion(entity.getIdRecepcion());
        dto.setNumeroCamion(entity.getNumeroCamion());
        dto.setDepartamento(entity.getDepartamento() != null ? entity.getDepartamento().name() : "MULTIPLE");
        dto.setDivision(entity.getDivision() != null ? entity.getDivision().name() : null);
        dto.setFechaRegistro(entity.getFechaRecepcion() != null ? entity.getFechaRecepcion().atStartOfDay() : null);
        dto.setEstado(entity.getEstado() != null ? entity.getEstado().name() : null);
        dto.setTotalEsperado(entity.getTotalEsperado());
        dto.setTotalRecibido(entity.getTotalRecibido());
        return dto;
    }

    private List<RecepcionCedisDetalleDTO> mapToDetalleDTOs(List<RecepcionCedisDetalle> detalles) {
        return detalles.stream().map(d -> {
            RecepcionCedisDetalleDTO dto = new RecepcionCedisDetalleDTO();
            dto.setIdProducto(d.getProducto().getIdProducto());
            dto.setNombreProducto(d.getProducto().getNombre());
            dto.setCantidadEsperada(d.getCantidadEsperada());
            dto.setCantidadRecibida(d.getCantidadRecibida());
            dto.setLote(d.getLote());
            dto.setFechaCaducidad(d.getFechaCaducidad());
            dto.setCantidadRpc(d.getCantidadRpc());
             dto.setTipoRpc(d.getTipoRpc());
            return dto;
        }).toList();
    }

    private List<DiscrepanciaDTO> mapToDiscrepanciaDTOs(List<DiscrepanciaRecepcion> discrepancias) {
        return discrepancias.stream().map(d -> {
            DiscrepanciaDTO dto = new DiscrepanciaDTO();
            dto.setIdDiscrepancia(d.getIdDiscrepancia());
            dto.setTotalEsperado(d.getTotalEsperado());
            dto.setTotalRecibido(d.getTotalRecibido());
            dto.setTotalFaltante(d.getTotalFaltante());
            return dto;
        }).toList();
    }

    private List<RpcDTO> mapToRpcDTOs(List<RpcControl> rpcList) {
        return rpcList.stream().map(r -> {
            RpcDTO dto = new RpcDTO();
            dto.setIdRpc(r.getIdRpc());
            dto.setTipoRpc(r.getTipoRpc().name());
            dto.setCantidadEntregada(r.getCantidadEntregada());
            dto.setCantidadRetornada(r.getCantidadRetornada());
            dto.setPendienteRetorno(r.isPendienteRetorno());
            dto.setFechaRegistro(r.getFechaRegistro());
            return dto;
        }).toList();
    }

    public RecepcionCedisResponseDTO construirRespuesta(RecepcionCedis recepcion) {
        RecepcionCedisResponseDTO dto = new RecepcionCedisResponseDTO();
        dto.setRecepcion(mapToRecepcionDTO(recepcion));
        dto.setDetalles(mapToDetalleDTOs(
                recepcionCedisDetalleRepository.findByRecepcionCedis_IdRecepcion(recepcion.getIdRecepcion())
        ));
        dto.setDiscrepancias(mapToDiscrepanciaDTOs(
                discrepanciaRepository.findByNumeroCamion(recepcion.getNumeroCamion())
        ));
        dto.setRpc(mapToRpcDTOs(
                rpcControlRepository.findByNumeroCamion(recepcion.getNumeroCamion())
        ));
        return dto;
    }
    @Transactional()
    public RecepcionCedisResponseDTO obtenerDatosCompletosCamion(String numeroCamion) {
        // 1. Buscamos la recepción con sus detalles cargados
        List<RecepcionCedis> recepciones = recepcionCedisRepository.findByNumeroCamion(numeroCamion);

        if (recepciones.isEmpty()) return null;

        // 2. Usamos la primera recepción como base (cabecera)
        RecepcionCedis principal = recepciones.get(0);

        RecepcionCedisResponseDTO dto = new RecepcionCedisResponseDTO();
        dto.setRecepcion(mapToRecepcionDTO(principal));
        dto.setDetalles(mapToDetalleDTOs(principal.getDetalles()));

        // 3. Traemos información satélite usando el número de camión
        dto.setDiscrepancias(mapToDiscrepanciaDTOs(discrepanciaRepository.findByNumeroCamion(numeroCamion)));
        dto.setRpc(mapToRpcDTOs(rpcControlRepository.findByNumeroCamion(numeroCamion)));


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
    private RecepcionCedisResponseDTO construirRespuestaVacia(String numeroCamion) {
        RecepcionCedis cabecera = new RecepcionCedis();
        cabecera.setNumeroCamion(numeroCamion);
        cabecera.setDepartamento(Departamento.MULTIPLE);
        cabecera.setDivision(Division.PERECEDEROS);
        cabecera.setEstado(EstadoRecepcion.PARCIAL);
        cabecera.setTotalEsperado(0);
        cabecera.setTotalRecibido(0);
        return construirRespuesta(cabecera);
    }


    public RecepcionAuditoriaDTO construirRespuestaPlano(RecepcionCedis recepcion) {
        RecepcionAuditoriaDTO dto = new RecepcionAuditoriaDTO();

        // 🔹 Cabecera de la recepción
        dto.setRecepcion(mapToRecepcionDTO(recepcion));

        // Detalles simplificados
        List<DetallePlanoDTO> detalles = recepcionCedisDetalleRepository
                .findByRecepcionCedis_IdRecepcion(recepcion.getIdRecepcion())
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

        // Discrepancias
        List<DiscrepanciaDTO> discrepancias = discrepanciaRepository.findByNumeroCamion(recepcion.getNumeroCamion())
                .stream()
                .map(d -> {
                    DiscrepanciaDTO disc = new DiscrepanciaDTO();
                    disc.setIdDiscrepancia(d.getIdDiscrepancia());
                    disc.setTotalEsperado(d.getTotalEsperado());
                    disc.setTotalRecibido(d.getTotalRecibido());
                    disc.setTotalFaltante(d.getTotalFaltante());
                    return disc;
                })
                .toList();
        dto.setDiscrepancias(discrepancias);

        // RPC
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
                .findByRecepcionCedis_IdRecepcion(idRecepcion)
                .stream()
                .mapToInt(RecepcionCedisDetalle::getCantidadRecibida)
                .sum();

        recepcion.setTotalRecibido(totalRecibido);
        recepcion.setPorcentajeAuditado((totalRecibido * 100.0) / recepcion.getTotalEsperado());
        recepcion.setCompleta(totalRecibido == recepcion.getTotalEsperado());
        recepcion.setEstado(EstadoRecepcion.CERRADA);

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

        RecepcionCedis principal = recepciones.get(0);
        RecepcionCedis cabecera = new RecepcionCedis();
        cabecera.setIdRecepcion(principal.getIdRecepcion());

        String nCamion = recepciones.get(0).getNumeroCamion(); // Guardamos el número exacto
        cabecera.setNumeroCamion(nCamion);
        cabecera.setDivision(recepciones.get(0).getDivision());
        cabecera.setFechaRecepcion(recepciones.get(0).getFechaRecepcion());
        cabecera.setDepartamento(Departamento.MULTIPLE);

        // Cálculos de totales
        int totalE = recepciones.stream().mapToInt(RecepcionCedis::getTotalEsperado).sum();
        int totalR = recepciones.stream().mapToInt(RecepcionCedis::getTotalRecibido).sum();

        cabecera.setTotalEsperado(totalE);
        cabecera.setTotalRecibido(totalR);
        cabecera.setPorcentajeAuditado((totalE > 0) ? (totalR * 100.0) / totalE : 0);
        cabecera.setCompleta(totalR == totalE);
        cabecera.setEstado(totalR == totalE ? EstadoRecepcion.ACEPTADA : EstadoRecepcion.PARCIAL);

        dto.setRecepcion(mapToRecepcionDTO(cabecera));

        // 2. Unir detalles de TODAS las recepciones de ese camión
        List<RecepcionCedisDetalle> todosDetalles = recepciones.stream()
                .flatMap(r -> recepcionCedisDetalleRepository
                        .findByRecepcionCedis_IdRecepcion(r.getIdRecepcion())
                        .stream())
                .collect(Collectors.toList()); // Usar collect para asegurar que es mutable

        dto.setDetalles(mapToDetalleDTOs(todosDetalles));

        // 3. ¡IMPORTANTE! Buscar discrepancias y RPC por el String exacto del camión
        dto.setDiscrepancias(mapToDiscrepanciaDTOs(discrepanciaRepository.findByNumeroCamion(nCamion)));
        dto.setRpc(mapToRpcDTOs(rpcControlRepository.findByNumeroCamion(nCamion)));

        return dto;
    }

    public RecepcionCedisResponseDTO obtenerPorCamionYDepartamento(String numeroCamion, String departamento) {
        List<RecepcionCedis> recepciones = recepcionCedisRepository.findByNumeroCamionAndDepartamento(
                numeroCamion, Enum.valueOf(Departamento.class, departamento.toUpperCase())
        );
        if (recepciones.isEmpty()) return null;
        return construirRespuesta(recepciones.get(0));
    }
}