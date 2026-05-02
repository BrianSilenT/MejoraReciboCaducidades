package com.bodegaaurrera.perecederos_demo.Service;

import com.bodegaaurrera.perecederos_demo.DTO.EtiquetaRequestDTO;
import com.bodegaaurrera.perecederos_demo.Model.Inventario;
import com.bodegaaurrera.perecederos_demo.Repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EtiquetaService {

    private final InventarioRepository inventarioRepository;

    public String generarZPL(
            String nombre,
            String upc,
            String lote,
            LocalDate caducidad,
            BigDecimal cantidad,
            int index,
            int total
    ) {
        return "^XA\n" +
                "^PW600\n" +
                "^LL400\n" +

                "^FO20,20^A0N,30,30^FD" + nombre + "^FS\n" +

                "^FO20,70^A0N,25,25^FDUPC: " + upc + "^FS\n" +
                "^FO20,110^A0N,25,25^FDLote: " + lote + "^FS\n" +

                "^FO20,150^A0N,25,25^FDCad: " + caducidad + "^FS\n" +

                "^FO20,190^A0N,30,30^FDCant: " + cantidad + "^FS\n" +

                "^FO20,240^BY2\n" +
                "^BCN,80,Y,N,N\n" +
                "^FD" + upc + "^FS\n" +

                "^FO400,20^A0N,25,25^FD" + index + "/" + total + "^FS\n" +

                "^XZ";
    }

    public List<String> generarEtiquetas(EtiquetaRequestDTO request) {

        List<Inventario> inventarios = inventarioRepository
                .findDetalleCompleto(request.getUpc());

        if (inventarios.isEmpty()) {
            throw new IllegalArgumentException("Producto no encontrado");
        }

        Inventario base = inventarios.stream()
                .filter(i -> request.getLote().equals(i.getLote()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));

        BigDecimal total = request.getCantidadTotal();
        int cajas = request.getNumeroCajas();

        if (cajas <= 0) {
            throw new IllegalArgumentException("Número de cajas inválido");
        }

        // 🔥 AQUÍ estaba tu error
        List<String> etiquetas = new ArrayList<>();

        BigDecimal porCaja = total.divide(BigDecimal.valueOf(cajas), 2, RoundingMode.DOWN);
        BigDecimal acumulado = BigDecimal.ZERO;

        for (int i = 1; i <= cajas; i++) {

            BigDecimal cantidadCaja = (i == cajas)
                    ? total.subtract(acumulado)
                    : porCaja;

            acumulado = acumulado.add(cantidadCaja);

            String zpl = generarZPL(
                    base.getProducto().getNombre(),
                    request.getUpc(),
                    request.getLote(),
                    base.getFechaCaducidad(),
                    cantidadCaja,
                    i,
                    cajas
            );

            etiquetas.add(zpl);
        }

        // 🔥 ESTE return te faltaba
        return etiquetas;
    }
}