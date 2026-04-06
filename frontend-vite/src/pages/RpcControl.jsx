import React, { useState, useEffect } from "react";
import axios from "axios";
import { 
  Box, Tabs, Tab, Typography, Paper, Table, TableBody, 
  TableCell, TableHead, TableRow, Button, Chip, CircularProgress 
} from "@mui/material";
import Navbar from "../components/Navbar";

function RpcDashboard() {
  const [tabIndex, setTabIndex] = useState(0); // 0 = Pendientes, 1 = Completados
  const [datos, setDatos] = useState([]);
  const [loading, setLoading] = useState(false);

  // Cargar datos cada vez que cambies de pestaña
  useEffect(() => {
    cargarDatos();
  }, [tabIndex]);

  const cargarDatos = async () => {
    setLoading(true);
    try {
      // Si tab es 0 busca /pendientes, si es 1 busca /completados
      const endpoint = tabIndex === 0 ? "/api/rpc/pendientes" : "/api/rpc/completados";
      const res = await axios.get(endpoint);
      setDatos(res.data.data || []);
    } catch (err) {
      console.error("Error al cargar RPCs", err);
    } finally {
      setLoading(false);
    }
  };

  const manejarRetorno = async (idRpc, max) => {
    const cant = window.prompt(`¿Cuántos RPCs recibiste físicamente? (Max: ${max})`, max);
    if (!cant) return;

    try {
      await axios.put(`/api/rpc/retorno/${idRpc}`, null, {
        params: { cantidadRetornada: parseInt(cant) }
      });
      alert("Movimiento registrado con éxito ✅");
      cargarDatos(); // Refrescar la lista actual
    } catch (err) {
      alert("Error al procesar el retorno ❌");
    }
  };

  return (
    <Box sx={{ flexGrow: 1 }}>
      <Navbar />
      <Box sx={{ p: 4, maxWidth: 1000, margin: "auto" }}>
        <Typography variant="h4" sx={{ mb: 3, fontWeight: "bold" }}>
          Control de Inventario RPC
        </Typography>

        <Paper sx={{ mb: 3 }}>
          <Tabs 
            value={tabIndex} 
            onChange={(e, newValue) => setTabIndex(newValue)} 
            indicatorColor="primary" 
            textColor="primary"
            centered
          >
            <Tab label="Pendientes de Retorno (Enviados)" />
            <Tab label="Historial de Completados (Recibidos)" />
          </Tabs>
        </Paper>

        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', mt: 5 }}><CircularProgress /></Box>
        ) : (
          <Paper elevation={3}>
            <Table>
              <TableHead sx={{ bgcolor: "#f5f5f5" }}>
                <TableRow>
                  <TableCell><strong>Camión</strong></TableCell>
                  <TableCell><strong>Tipo</strong></TableCell>
                  <TableCell align="center"><strong>Enviados</strong></TableCell>
                  <TableCell align="center"><strong>Recibidos</strong></TableCell>
                  <TableCell align="center"><strong>Fecha</strong></TableCell>
                  <TableCell align="center"><strong>Acción</strong></TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {datos.map((rpc) => (
                  <TableRow key={rpc.idRpc}>
                    <TableCell>{rpc.numeroCamion}</TableCell>
                    <TableCell><Chip label={rpc.tipoRpc} size="small" /></TableCell>
                    <TableCell align="center">{rpc.cantidadEntregada}</TableCell>
                    <TableCell align="center">{rpc.cantidadRetornada}</TableCell>
                    <TableCell align="center">{rpc.fechaRegistro}</TableCell>
                    <TableCell align="center">
                      {tabIndex === 0 ? (
                        <Button 
                          variant="contained" 
                          color="warning" 
                          size="small"
                          onClick={() => manejarRetorno(rpc.idRpc, rpc.cantidadEntregada)}
                        >
                          Recibir Retorno
                        </Button>
                      ) : (
                        <Chip label="Cerrado" color="success" variant="outlined" />
                      )}
                    </TableCell>
                  </TableRow>
                ))}
                {datos.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={6} align="center" sx={{ py: 3 }}>
                      No hay registros en esta categoría.
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </Paper>
        )}
      </Box>
    </Box>
  );
}

export default RpcDashboard;
