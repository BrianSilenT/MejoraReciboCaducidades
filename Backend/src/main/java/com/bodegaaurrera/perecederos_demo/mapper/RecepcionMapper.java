package com.bodegaaurrera.perecederos_demo.mapper;

import com.bodegaaurrera.perecederos_demo.DTO.*;
import com.bodegaaurrera.perecederos_demo.Enums.Departamento;
import com.bodegaaurrera.perecederos_demo.Enums.Division;
import com.bodegaaurrera.perecederos_demo.Enums.EstadoRecepcion;
import com.bodegaaurrera.perecederos_demo.Model.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Component
public class RecepcionMapper {

    // --- Entidad a DTO ---

    public RecepcionCedisDTO toRecepcionDTO(RecepcionCedis entity) {
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

    public List<RecepcionCedisDetalleDTO> toDetalleDTOs(List<RecepcionCedisDetalle> detalles) {
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

    public List<DiscrepanciaDTO> toDiscrepanciaDTOs(List<DiscrepanciaRecepcion> discrepancias) {
        return discrepancias.stream().map(d -> {
            DiscrepanciaDTO dto = new DiscrepanciaDTO();
            dto.setIdDiscrepancia(d.getIdDiscrepancia());
            dto.setTotalEsperado(d.getTotalEsperado());
            dto.setTotalRecibido(d.getTotalRecibido());
            dto.setTotalFaltante(d.getTotalFaltante());
            return dto;
        }).toList();
    }

    public List<RpcDTO> toRpcDTOs(List<RpcControl> rpcList) {
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

    // --- DTO a Entidad (Para registrar) ---


    public RecepcionCedis fromRequestToEntity(
            RecepcionCedisRequestDTO request,
            BigDecimal totalRecibido
    ) {
        RecepcionCedis recepcion = new RecepcionCedis();

        recepcion.setNumeroCamion(request.getNumeroCamion());
        recepcion.setDepartamento(Departamento.valueOf(request.getDepartamento().toUpperCase()));
        recepcion.setDivision(Division.valueOf(request.getDivision().toUpperCase()));

        recepcion.setTotalEsperado(request.getTotalEsperado()); // ya debe ser BigDecimal
        recepcion.setFechaRecepcion(LocalDate.now());

        recepcion.setTotalRecibido(totalRecibido);

        // 🔥 porcentaje seguro
        if (request.getTotalEsperado().compareTo(BigDecimal.ZERO) == 0) {
            recepcion.setPorcentajeAuditado(0.0);
        } else {
            double porcentaje = totalRecibido
                    .divide(request.getTotalEsperado(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();

            recepcion.setPorcentajeAuditado(porcentaje);
        }

        // 🔥 estado correcto
        if (totalRecibido.compareTo(BigDecimal.ZERO) == 0) {
            recepcion.setEstado(EstadoRecepcion.RECHAZADA);
        } else if (totalRecibido.compareTo(request.getTotalEsperado()) < 0) {
            recepcion.setEstado(EstadoRecepcion.PARCIAL);
        } else {
            recepcion.setEstado(EstadoRecepcion.ACEPTADA);
        }

        recepcion.setCompleta(
                totalRecibido.compareTo(request.getTotalEsperado()) == 0
        );

        return recepcion;
    }

    public DetallePlanoDTO toDetallePlanoDTO(RecepcionCedisDetalle d) {
        if (d == null) return null;

        DetallePlanoDTO det = new DetallePlanoDTO();
        det.setIdDetalleCedis(d.getIdDetalleCedis());
        // Extraemos el nombre directamente del objeto Producto relacionado
        det.setProducto(d.getProducto() != null ? d.getProducto().getNombre() : "Producto no identificado");
        det.setCantidadRecibida(d.getCantidadRecibida());
        det.setLote(d.getLote());
        det.setFechaCaducidad(d.getFechaCaducidad());
        det.setCantidadRpc(d.getCantidadRpc());

        return det;
    }

    // Dentro de tu Mapper
    public RpcResumenDTO toResumenDTO(long registradas, long pendientes, long completadas,
                                      long entregado, long retornado) {
        return RpcResumenDTO.builder()
                .totalRegistradas(registradas)
                .totalPendientes(pendientes)
                .totalCompletadas(completadas)
                .totalEntregado(entregado)
                .totalRetornado(retornado)
                .totalFaltante(entregado - retornado)
                .build();
    }

    public ProductoDTO toProductoDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getIdProducto());
        dto.setNombre(producto.getNombre());
        dto.setCodigoBarras(producto.getCodigoBarras());
        dto.setPrecio(producto.getPrecio()); // si tienes
        return dto;
    }

}