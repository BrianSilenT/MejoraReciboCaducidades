package com.bodegaaurrera.perecederos_demo.Controller;

import com.bodegaaurrera.perecederos_demo.Repository.InventarioRepository;
import com.bodegaaurrera.perecederos_demo.Repository.RecepcionCedisRepository;
import com.bodegaaurrera.perecederos_demo.Repository.RecepcionRepository;
import com.bodegaaurrera.perecederos_demo.Repository.RpcControlRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final InventarioRepository inventarioRepo;
    private final RecepcionCedisRepository recepcionCedisRepo;
    private final RecepcionRepository recepcionProveedorRepo;
    private final RpcControlRepository rpcRepo;

    public DashboardController(
            InventarioRepository inventarioRepo,
            RecepcionCedisRepository recepcionCedisRepo,
            RecepcionRepository recepcionProveedorRepo,
            RpcControlRepository rpcRepo
    ) {
        this.inventarioRepo = inventarioRepo;
        this.recepcionCedisRepo = recepcionCedisRepo;
        this.recepcionProveedorRepo = recepcionProveedorRepo;
        this.rpcRepo = rpcRepo;
    }

    @GetMapping
    public Map<String, Object> getDashboard() {
        Map<String, Object> resumen = new HashMap<>();

        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(7);

        // 🔹 Inventario
        long caducados = inventarioRepo.countByFechaCaducidadBefore(hoy);
        long porCaducar = inventarioRepo.countByFechaCaducidadBetween(hoy, limite);
        long inventarioTotal = inventarioRepo.sumCantidad();

        // 🔹 Recepciones
        long recepcionesCedis = recepcionCedisRepo.count();
        long recepcionesProveedor = recepcionProveedorRepo.count();

        // 🔹 RPC
        long rpcPendientes = rpcRepo.countByPendienteRetorno(true);

        // 🔹 Construir respuesta
        resumen.put("caducados", caducados);
        resumen.put("porCaducar", porCaducar);
        resumen.put("inventarioTotal", inventarioTotal);
        resumen.put("recepcionesCedis", recepcionesCedis);
        resumen.put("recepcionesProveedor", recepcionesProveedor);
        resumen.put("rpcPendientes", rpcPendientes);

        return resumen;
    }
}