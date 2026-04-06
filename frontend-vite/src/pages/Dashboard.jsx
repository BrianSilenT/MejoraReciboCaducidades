import React, { useEffect, useState } from "react";
import axios from "axios";
import { Grid, Card, CardContent, Typography, Button, Alert } from "@mui/material";
import { useNavigate } from "react-router-dom";
import ErrorIcon from "@mui/icons-material/Error";
import WarningIcon from "@mui/icons-material/Warning";
import InfoIcon from "@mui/icons-material/Info";
import Navbar from "../components/Navbar";

function Dashboard() {
  const [alertasInventario, setAlertasInventario] = useState([]);
  const navigate = useNavigate();

  // 🔹 Recuperar usuario y tienda desde localStorage
  const usuario = localStorage.getItem("usuario");
  const tienda = localStorage.getItem("tienda");

  useEffect(() => {
    const fetchData = async () => {
      const resAlertas = await axios.get(`/api/inventario/alertas`);
      let datos = resAlertas.data.data;

      // 🔹 Eliminar duplicados (codigoBarras + fechaCaducidad + cantidad)
      const unique = [];
      const seen = new Set();
      for (const d of datos) {
        const key = `${d.codigoBarras}-${d.fechaCaducidad}-${d.cantidad}`;
        if (!seen.has(key)) {
          seen.add(key);
          unique.push(d);
        }
      }

      setAlertasInventario(unique);
    };
    fetchData();
  }, []);

  // 🔹 Función para aplicar reglas de negocio
  const filtrarAlertas = (alertas) => {
    return alertas.filter((a) => {
      if (a.alerta.includes("✅")) return false;

      const desc = a.descripcion?.toLowerCase() || "";

      if (desc.includes("pollo") || desc.includes("carne") || desc.includes("salchicha")) {
        return a.diasRestantes <= 3;
      }
      if (desc.includes("paracetamol") || desc.includes("farmacia")) {
        return a.diasRestantes <= 30;
      }
      if (desc.includes("fruta") || desc.includes("verdura") || desc.includes("leche")) {
        return a.diasRestantes <= 1;
      }
      return a.diasRestantes < 0;
    });
  };

  // 🔹 Próximos a caducar (<10 días, pero aún no en retiro)
  const proximosCaducar = alertasInventario.filter((a) => {
    if (a.alerta.includes("✅") || a.diasRestantes < 0) return false;
    return a.diasRestantes <= 10;
  });

  const alertasFiltradas = filtrarAlertas(alertasInventario);

  return (
    <div style={{ padding: 40 }}>
      <Navbar />

      {/* Encabezado personalizado */}
      <Typography variant="h4" gutterBottom>
        Bienvenido {usuario || "Usuario"}
      </Typography>
      <Typography variant="h6" gutterBottom>
        Tienda: {tienda || "No especificada"}
      </Typography>

      <Typography variant="h5" gutterBottom>
        Dashboard General
      </Typography>

      {/* Tarjetas resumen */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Typography variant="h6">
                <ErrorIcon color="error" /> Caducados
              </Typography>
              <Typography variant="h4" color="error">
                {alertasFiltradas.filter((a) => a.diasRestantes < 0).length}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Typography variant="h6">
                <WarningIcon color="warning" /> Próximos a caducar (&lt; 10 días)
              </Typography>
              <Typography variant="h4" color="warning.main">
                {proximosCaducar.length}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Typography variant="h6">
                <InfoIcon color="info" /> Total alertas críticas
              </Typography>
              <Typography variant="h4" color="info.main">
                {alertasFiltradas.length}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Botones de navegación */}
      <div style={{ marginTop: 40, textAlign: "center" }}>
        <Button variant="contained" color="primary" onClick={() => navigate("/inventario")} style={{ marginRight: 20 }}>
          Inventario
        </Button>
        <Button variant="contained" color="secondary" onClick={() => navigate("/recepcion")} style={{ marginRight: 20 }}>
          Recepción Proveedores
        </Button>
        <Button variant="contained" color="success" onClick={() => navigate("/recepcion-cedis")} style={{ marginRight: 20 }}>
          Recepción CEDIS
        </Button>
        <Button variant="contained" color="warning" onClick={() => navigate("/rpc")}>
          Control RPC
        </Button>
      </div>

      {/* Alertas detalladas */}
      <div style={{ marginTop: 40 }}>
        <Typography variant="h5">Detalle de Alertas de Inventario</Typography>
        {alertasFiltradas.length === 0 && proximosCaducar.length === 0 ? (
          <Typography>No hay alertas de inventario</Typography>
        ) : (
          <>
            {alertasFiltradas.map((alerta, i) => (
              <Alert
                key={i}
                severity={alerta.diasRestantes < 0 ? "error" : "warning"}
                style={{ marginTop: 10 }}
                action={
                  <Button
                    color="inherit"
                    size="small"
                    onClick={() => navigate(`/inventario/${alerta.codigoBarras}`)}
                  >
                    Ver detalle
                  </Button>
                }
              >
                {alerta.descripcion} — {alerta.cantidad} piezas — caduca el{" "}
                {alerta.fechaCaducidad} ({alerta.alerta})
              </Alert>
            ))}

            {proximosCaducar.map((alerta, i) => {
              const desc = alerta.descripcion?.toLowerCase() || "";
              const esFarmacia = desc.includes("paracetamol") || desc.includes("farmacia");

              // Condición: no farmacia, menos de 10 días, pero aún no en retiro
              const surteInmediatamente =
                !esFarmacia && alerta.diasRestantes > 3 && alerta.diasRestantes <= 10;

              return (
                <Alert
                  key={`prox-${i}`}
                  severity={surteInmediatamente ? "warning" : "info"}
                  style={{ marginTop: 10 }}
                  action={
                    <Button
                      color="inherit"
                      size="small"
                      onClick={() => navigate(`/inventario/${alerta.codigoBarras}`)}
                    >
                      Ver detalle
                    </Button>
                  }
                >
                  {alerta.descripcion} — {alerta.cantidad} piezas — caduca el{" "}
                  {alerta.fechaCaducidad} ({alerta.alerta})
                  {surteInmediatamente && (
                    <strong style={{ marginLeft: 10 }}>⚡ Surte inmediatamente</strong>
                  )}
                </Alert>
              );
            })}
          </>
        )}
      </div>
    </div>
  );
}

export default Dashboard;