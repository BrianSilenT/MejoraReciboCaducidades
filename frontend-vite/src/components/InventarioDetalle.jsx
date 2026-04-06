import React, { useEffect, useState } from "react";
import axios from "axios";
import { useParams, useNavigate } from "react-router-dom";
import { Card, CardContent, Typography, Alert, Button } from "@mui/material";
import Navbar from "../components/Navbar";

function InventarioDetalle() {
  const { codigoBarras } = useParams();
  const [producto, setProducto] = useState([]);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProducto = async () => {
      try {
        const res = await axios.get(`/api/inventario/alertas`);
        let datos = res.data.data.filter(p => p.codigoBarras === codigoBarras);

        if (datos.length > 0) {
          // 🔹 Eliminar duplicados (fechaCaducidad + cantidad)
          const unique = [];
          const seen = new Set();
          for (const d of datos) {
            const key = `${d.fechaCaducidad}-${d.cantidad}`;
            if (!seen.has(key)) {
              seen.add(key);
              unique.push(d);
            }
          }

          // 🔹 Ordenar por fecha de caducidad (más cercana primero)
          unique.sort((a, b) => new Date(a.fechaCaducidad) - new Date(b.fechaCaducidad));
          setProducto(unique);
          setError("");
        } else {
          setError("Producto no encontrado");
        }
      } catch (err) {
        setError("Error al cargar inventario");
      }
    };
    fetchProducto();
  }, [codigoBarras]);

  const generarAlertas = (p) => {
    const alertas = [];
    const hoy = new Date();
    const caducidad = new Date(p.fechaCaducidad);
    const diferenciaDias = Math.ceil((caducidad - hoy) / (1000 * 60 * 60 * 24));

    if (diferenciaDias < 0) {
      alertas.push({ tipo: "error", mensaje: "❌ Producto caducado" });
    } else if (diferenciaDias <= 7) {
      alertas.push({ tipo: "warning", mensaje: `⚠️ Producto por caducar en ${diferenciaDias} días` });
    } else {
      alertas.push({ tipo: "success", mensaje: `✅ Producto vigente, caduca en ${diferenciaDias} días` });
    }

    if (p.cantidad < 10) {
      alertas.push({ tipo: "info", mensaje: "ℹ️ Stock bajo" });
    }

    // ⚡ Surte inmediatamente (no farmacia, entre 4 y 10 días)
    const desc = p.descripcion?.toLowerCase() || "";
    const esFarmacia = desc.includes("paracetamol") || desc.includes("farmacia");
    if (!esFarmacia && diferenciaDias > 3 && diferenciaDias <= 10) {
      alertas.push({ tipo: "warning", mensaje: "⚡ Surte inmediatamente — reducir merma y rotar producto" });
    }

    return alertas;
  };

  if (error) {
    return (
      <div style={{ padding: 40 }}>
        <Navbar />
        <Alert severity="error">{error}</Alert>
        <Button
          variant="contained"
          color="primary"
          style={{ marginTop: 20 }}
          onClick={() => navigate("/inventario")}
        >
          Volver al Inventario
        </Button>
      </div>
    );
  }

  if (producto.length === 0) {
    return <Typography style={{ padding: 40 }}>Cargando detalle del producto...</Typography>;
  }

  return (
    <div style={{ padding: 40 }}>
      <Navbar />
      <Typography variant="h4" gutterBottom>Detalle de Producto</Typography>
      <Typography variant="h5">{producto[0].descripcion || "Sin descripción"} (Código: {codigoBarras})</Typography>

      {producto.map((p, i) => (
        <Card key={i} style={{ marginTop: 20 }}>
          <CardContent>
            <Typography>Cantidad: {p.cantidad}</Typography>
            <Typography>Fecha de caducidad: {p.fechaCaducidad}</Typography>
            <Typography>Días restantes: {p.diasRestantes}</Typography>

            {generarAlertas(p).map((a, j) => (
              <Alert key={j} severity={a.tipo} style={{ marginTop: 10 }}>
                {a.mensaje}
              </Alert>
            ))}
          </CardContent>
        </Card>
      ))}

      <div style={{ marginTop: 20 }}>
        <Button variant="contained" color="primary" onClick={() => navigate("/inventario")}>
          Volver al Inventario
        </Button>
      </div>
    </div>
  );
}

export default InventarioDetalle;