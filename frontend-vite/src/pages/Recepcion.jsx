import React, { useState } from "react";
import axios from "axios";
import { Grid, TextField, Button, Alert, Card, CardContent, Typography } from "@mui/material";
import Navbar from "../components/Navbar";

function Recepcion() {
  const [po, setPo] = useState("");
  const [error, setError] = useState("");
  const [orden, setOrden] = useState(null);
  const [capturas, setCapturas] = useState({});
  const [mensaje, setMensaje] = useState("");

  const buscarOrden = async () => {
    try {
      const vigenteRes = await axios.get(`/api/ordenes-compra/${po}/vigente`);
      if (!vigenteRes.data.data.toLowerCase().includes("vigente")) {
        setError("Orden expirada o no encontrada");
        setOrden(null);
        return;
      }

      const res = await axios.get(`/api/ordenes-compra/${po}`);
      setOrden(res.data.data);
      setError("");
      setCapturas({});
      setMensaje("");
    } catch (err) {
      setError("Error al buscar la orden");
      setOrden(null);
    }
  };

  const handleChange = (codigoBarras, field, value) => {
    setCapturas((prev) => ({
      ...prev,
      [codigoBarras]: {
        ...prev[codigoBarras],
        [field]: value,
      },
    }));
  };

  const generarAlertas = (p) => {
    const datos = capturas[p.producto.codigoBarras];
    if (!datos) return [];

    const alertas = [];
    const hoy = new Date();

    if (datos.cantidad && datos.cantidad < p.cantidadEsperada) {
      alertas.push({
        tipo: "warning",
        mensaje: `Cantidad recibida menor a la esperada (${datos.cantidad}/${p.cantidadEsperada})`,
      });
    }

    if (datos.caducidad) {
      const caducidad = new Date(datos.caducidad);
      if (caducidad < hoy) {
        alertas.push({ tipo: "error", mensaje: "Producto caducado" });
      }
    }

    return alertas;
  };

  const finalizarRecepcion = async () => {
    const hoy = new Date().toISOString().split("T")[0];
    for (const p of orden.productos) {
      const datos = capturas[p.producto.codigoBarras];
      if (datos?.caducidad) {
        const fechaRecepcion = new Date(hoy);
        const fechaCaducidad = new Date(datos.caducidad);
        const diasVida = Math.floor((fechaCaducidad - fechaRecepcion) / (1000 * 60 * 60 * 24));

        if (diasVida < 10) {
          setMensaje(
            `El producto ${p.producto.descripcion} tiene solo ${diasVida} días de vida útil. No se puede recibir. ❌`
          );
          return;
        }
      }
    }

    const payload = {
      ordenCompra: { idOrden: parseInt(po, 10) },
      fechaRecepcion: hoy,
      productos: orden.productos.map((p) => ({
        producto: {
          idProducto: p.producto.idProducto,
          codigoBarras: p.producto.codigoBarras,
        },
        cantidadRecibida: parseInt(capturas[p.producto.codigoBarras]?.cantidad, 10),
        lote: capturas[p.producto.codigoBarras]?.lote,
        fechaCaducidad: capturas[p.producto.codigoBarras]?.caducidad,
      })),
    };

    try {
      console.log("Payload enviado:", payload);
      await axios.post("/api/recepcion", payload);
      setMensaje("Recepción registrada correctamente ✅");
      setOrden(null);
      setPo("");
    } catch (err) {
      setMensaje("Error al registrar la recepción ❌");
    }
  };

  return (
    <div style={{ width: 600, margin: "auto", marginTop: 50 }}>
      <Navbar />
      <Typography variant="h4" gutterBottom>
        Recepción de Orden (Proveedores)
      </Typography>

      <TextField
        label="PO #"
        fullWidth
        margin="normal"
        value={po}
        onChange={(e) => setPo(e.target.value)}
      />
      <Button variant="contained" color="primary" onClick={buscarOrden}>
        Confirmar
      </Button>

      {error && <Alert severity="error" style={{ marginTop: 20 }}>{error}</Alert>}
      {mensaje && (
        <Alert severity={mensaje.includes("Error") ? "error" : "success"} style={{ marginTop: 20 }}>
          {mensaje}
        </Alert>
      )}

      {orden && (
        <div style={{ marginTop: 30 }}>
          <Typography variant="h6" gutterBottom>
            Productos en la orden
          </Typography>

          <Grid container spacing={2}>
            {orden.productos.map((p) => (
              <Grid item xs={12} key={p.producto.codigoBarras}>
                <Card>
                  <CardContent>
                    <Typography variant="subtitle1">
                      {p.producto.descripcion} — Esperado: {p.cantidadEsperada}
                    </Typography>

                    <TextField
                      label="Cantidad recibida"
                      type="number"
                      value={capturas[p.producto.codigoBarras]?.cantidad || ""}
                      onChange={(e) =>
                        handleChange(p.producto.codigoBarras, "cantidad", e.target.value)
                      }
                      style={{ marginRight: 10, marginTop: 10 }}
                    />
                    <TextField
                      label="Lote"
                      value={capturas[p.producto.codigoBarras]?.lote || ""}
                      onChange={(e) =>
                        handleChange(p.producto.codigoBarras, "lote", e.target.value)
                      }
                      style={{ marginRight: 10, marginTop: 10 }}
                    />
                    <TextField
                      label="Fecha caducidad"
                      type="date"
                      value={capturas[p.producto.codigoBarras]?.caducidad || ""}
                      onChange={(e) =>
                        handleChange(p.producto.codigoBarras, "caducidad", e.target.value)
                      }
                      style={{ marginTop: 10 }}
                    />

                    {generarAlertas(p).map((a, i) => (
                      <Alert key={i} severity={a.tipo} style={{ marginTop: 10 }}>
                        {a.mensaje}
                      </Alert>
                    ))}
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>

          <div style={{ marginTop: 20, textAlign: "center" }}>
            <Button variant="contained" color="success" onClick={finalizarRecepcion}>
              Finalizar recepción
            </Button>
          </div>
        </div>
      )}
    </div>
  );
}

export default Recepcion;