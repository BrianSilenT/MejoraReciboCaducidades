import React from "react";
import { Grid, Alert, Typography, Card, CardContent } from "@mui/material";
import Navbar from "../components/Navbar";

function Discrepancias({ discrepancias }) {
  if (!discrepancias || discrepancias.length === 0) {
    return null;
  }

  return (
    <div style={{ marginTop: 30 }}>
      <Navbar />
      <Typography variant="h5" gutterBottom>
        Discrepancias detectadas
      </Typography>

      <Grid container spacing={2}>
        {discrepancias.map((d, i) => (
          <Grid item xs={12} key={i}>
            <Card>
              <CardContent>
                <Typography variant="subtitle1">
                  {d.descripcion} (Código: {d.codigoBarras})
                </Typography>
                <Typography>
                  Esperado: {d.cantidadEsperada} — Recibido: {d.cantidadRecibida}
                </Typography>
                <Alert severity="warning" style={{ marginTop: 10 }}>
                  Diferencia: {d.cantidadEsperada - d.cantidadRecibida}
                </Alert>
                {d.caducidadInvalida && (
                  <Alert severity="error" style={{ marginTop: 10 }}>
                    Fecha de caducidad inválida o vencida
                  </Alert>
                )}
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    </div>
  );
}

export default Discrepancias;