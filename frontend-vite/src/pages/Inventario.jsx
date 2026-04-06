import React, { useState } from "react";
import axios from "axios";
import { Grid, TextField, Button, Alert, Card, CardContent, Typography } from "@mui/material";
import Navbar from "../components/Navbar";

function Inventario() {
  const [codigo, setCodigo] = useState("");
  const [lotes, setLotes] = useState([]);
  const [error, setError] = useState("");

  // buscarProducto
const buscarProducto = async () => {
  try {
    const res = await axios.get(`/api/inventario/buscar?codigoBarras=${codigo}`);
    let datos = res.data.data;

    if (!Array.isArray(datos)) {
      datos = datos ? [datos] : [];
    }

    if (datos.length === 0) {
      setError("UPC no registrado en inventario");
      setLotes([]);
      return;
    }

    const hoy = new Date();
    datos.forEach(d => {
      const caducidad = new Date(d.fechaCaducidad);
      d.diasRestantes = Math.ceil((caducidad - hoy) / (1000 * 60 * 60 * 24));
    });

    datos.sort((a, b) => new Date(a.fechaCaducidad) - new Date(b.fechaCaducidad));

    setLotes(datos);
    setError("");
  } catch (err) {
    setError("Error al cargar inventario");
    setLotes([]);
  }
};

 // generarAlertas
const generarAlertas = (lote) => {
  const alertas = [];
  const diferenciaDias = lote.diasRestantes ?? Math.ceil(
    (new Date(lote.fechaCaducidad) - new Date()) / (1000 * 60 * 60 * 24)
  );

  if (diferenciaDias < 0) {
    alertas.push({ tipo: "error", mensaje: "❌ Producto caducado" });
  } else if (diferenciaDias <= 7) {
    alertas.push({ tipo: "warning", mensaje: `⚠️ Producto por caducar en ${diferenciaDias} días` });
  } else {
    alertas.push({ tipo: "success", mensaje: `✅ Producto vigente, caduca en ${diferenciaDias} días` });
  }

  if (lote.cantidad < 10) {
    alertas.push({ tipo: "info", mensaje: "ℹ️ Stock bajo" });
  }

  const desc = lote.descripcion?.toLowerCase() || "";
  const esFarmacia = desc.includes("paracetamol") || desc.includes("farmacia");
  if (!esFarmacia && diferenciaDias > 3 && diferenciaDias <= 10) {
    alertas.push({ tipo: "warning", mensaje: "⚡ Surte inmediatamente — reducir merma y rotar producto" });
  }

  return alertas;
};

  return (
    <Grid container justifyContent="center" style={{ marginTop: 50 }}>
      <Grid item xs={12} md={6}>
        <Navbar />
        <Typography variant="h4" gutterBottom>Inventario</Typography>

        <TextField
          label="Código de barras"
          fullWidth
          margin="normal"
          value={codigo}
          onChange={(e) => setCodigo(e.target.value)}
        />
        <Button variant="contained" color="primary" onClick={buscarProducto}>Buscar</Button>

        {error && <Alert severity="error" style={{ marginTop: 20 }}>{error}</Alert>}

        {lotes.length > 0 && (
          <div style={{ marginTop: 30 }}>
            <Typography variant="h6">{lotes[0].descripcion || "Producto"}</Typography>
            <Typography>Stock total: {lotes.reduce((acc, l) => acc + l.cantidad, 0)}</Typography>

            {lotes.map((lote, i) => (
              <Card key={i} style={{ marginTop: 20 }}>
                <CardContent>
                  <Typography>Lote #{i + 1}</Typography>
                  <Typography>Cantidad: {lote.cantidad}</Typography>
                  <Typography>Fecha de caducidad: {lote.fechaCaducidad}</Typography>
                  <Typography>Días restantes: {lote.diasRestantes}</Typography>

                  {generarAlertas(lote).map((a, j) => (
                    <Alert key={j} severity={a.tipo} style={{ marginTop: 10 }}>
                      {a.mensaje}
                    </Alert>
                  ))}
                </CardContent>
              </Card>
            ))}
          </div>
        )}
      </Grid>
    </Grid>
  );
}

export default Inventario;