import React, { useState, useEffect } from "react";
import axios from "axios";
import { DataGrid } from "@mui/x-data-grid";
import { Button, Typography, Alert, Select, MenuItem, TextField } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import Discrepancias from "./Discrepancias";
import Navbar from "../components/Navbar";

function RecepcionCedis() {
  const [camiones, setCamiones] = useState([]);
  const [camionSeleccionado, setCamionSeleccionado] = useState(null);
  const [productos, setProductos] = useState([]);
  const [rpc, setRpc] = useState([]);
  const [capturas, setCapturas] = useState({});
  const [discrepancias, setDiscrepancias] = useState([]);
  const [mensaje, setMensaje] = useState("");
  const [departamentoSeleccionado, setDepartamentoSeleccionado] = useState("");
  const [idActual, setIdActual] = useState(null);

  
 const normalizarDetalles = (detalles, catalogo) =>
  (detalles || []).map((d, i) => ({
    id: `${d.idProducto}-${i}`, // id único
    idProducto: d.idProducto,
    descripcion: catalogo?.[d.idProducto] || `Producto ${d.idProducto}`, 
    cantidadEsperada: d.cantidadEsperada || 0, 
    cantidadRecibida: "",  
    lote: "",              
    fechaCaducidad: null,  
    rpcCantidad: "",       
    rpcTipo: ""             
  }));

  useEffect(() => {
    axios.get("/api/recepciones/cedis/camiones")
      .then(res => setCamiones(res.data.data || []))
      .catch(() => setCamiones([]));
  }, []);




const cargarCamionCompleto = async () => {
  if (!camionSeleccionado) {
    setMensaje("Por favor, selecciona un camión primero ⚠️");
    return;
  }

  try {
    const res = await axios.get(`/api/recepciones/cedis/camion/${camionSeleccionado}`);
    
    // 🔥 CORRECCIÓN AQUÍ: Accedemos al segundo nivel de .data
    const respuestaServidor = res.data.data; 

    // Ahora respuestaServidor ya es el objeto que contiene { recepcion, detalles, etc. }
    if (respuestaServidor && respuestaServidor.recepcion) {
      
      const idDeLaDb = respuestaServidor.recepcion.idRecepcion;
      
      // Si el id es null o undefined, lanzamos la alerta
      if (!idDeLaDb) {
        setMensaje("El camión existe pero la recepción no tiene ID activo ⚠️");
        setIdActual(null);
        return;
      }

      setIdActual(idDeLaDb); 

      // Cargamos los productos usando el helper
      setProductos(normalizarDetalles(respuestaServidor.detalles || []));
      setDiscrepancias(respuestaServidor.discrepancias || []);

      setMensaje(`Camión ${camionSeleccionado} cargado con ID: ${idDeLaDb} ✅`);
    } else {
      setMensaje("El camión no tiene una recepción activa ⚠️");
      setIdActual(null);
    }
  } catch (err) {
    console.error("Error al cargar:", err);
    setMensaje("Error de conexión o servidor ❌");
    setIdActual(null);
  }
};

  const cargarPorDepartamento = async (departamento) => {
  try {
    setDepartamentoSeleccionado(departamento);
    const res = await axios.get(`/api/recepciones/cedis/camion/${camionSeleccionado}/departamento/${departamento}`);
    setProductos(normalizarDetalles(res.data.data?.detalles));
    setRpc(res.data.data?.rpc || []);
    setMensaje("");
  } catch {
    setProductos([]);
    setRpc([]);
    setMensaje(`No se encontraron productos para el departamento ${departamento} ❌`);
  }
};

  const actualizarDetalle = (id, cambios) => {
  setProductos(prev =>
    prev.map(p =>
      p.id === id ? { ...p, ...cambios } : p
    )
  );
};

 const finalizarRecepcion = async () => {
  const payload = {
    numeroCamion: camionSeleccionado,
    division: "PERECEDEROS",
    departamento: departamentoSeleccionado || "MULTIPLE",
    totalEsperado: productos.reduce((sum, p) => sum + (parseInt(p.cantidadEsperada) || 0), 0),
    detalles: productos.map(p => {
      // Mapeo manual para asegurar que coincida con el Enum TipoRpc de Java
      let rpcMapeado = null;
      if (p.rpcTipo) {
        // Quitamos espacios: "TIPO 1" -> "TIPO1", "CAJA CARTON" -> "CAJA_CARTON"
        rpcMapeado = p.rpcTipo.replace(/\s+/g, '').toUpperCase();
        
        // Caso especial para CAJA_CARTON si en tu UI dice "CAJA CARTON"
        if (rpcMapeado === "CAJACARTON") rpcMapeado = "CAJA_CARTON";
      }

      return {
        idProducto: p.idProducto,
        cantidadEsperada: parseInt(p.cantidadEsperada) || 0,
        cantidadRecibida: parseInt(p.cantidadRecibida) || 0,
        lote: p.lote || "SN-LOTE",
        fechaCaducidad: p.fechaCaducidad ? new Date(p.fechaCaducidad).toISOString().split('T')[0] : null,
        cantidadRpc: p.rpcCantidad ? parseInt(p.rpcCantidad) : 0,
        tipoRpc: rpcMapeado // Enviamos TIPO1, TIPO2, etc.
      };
    })
  };

  console.log("Enviando Payload con Enums corregidos:", payload);
try {
    const res = await axios.post("/api/recepciones/cedis", payload);
    
    // RUTA CORRECTA SEGÚN POSTMAN:
    const nuevoId = res.data.recepcion?.idRecepcion;
    
    if (nuevoId) {
      setIdActual(nuevoId); 
      setMensaje(`Registro guardado (ID: ${nuevoId}). Ahora puedes Cerrar ✅`);
    }
  } catch (error) {
    setMensaje("Error al finalizar ❌");
  }
};
const cerrarRecepcion = async () => {
  if (!idActual) {
    setMensaje("No se puede cerrar: falta el ID de la recepción. Re-selecciona el camión ❌");
    return;
  }

  try {
    // Enviamos el ID al endpoint que definiste en Java: @PutMapping("/{id}/cerrar")
    const res = await axios.put(`/api/recepciones/cedis/${idActual}/cerrar`);
    
    // El Service de Java devuelve el RecepcionAuditoriaDTO con las discrepancias
    if (res.data.discrepancias) {
      setDiscrepancias(res.data.discrepancias);
    }
    
    setMensaje("Paso 2 completado: Recepción CERRADA y auditada con éxito 🔒");
    // Opcional: setIdActual(null); // Limpiar después de cerrar
  } catch (error) {
    setMensaje("Error del servidor al intentar cerrar la recepción ❌");
  }
};
    return (
  <div style={{ width: 700, margin: "auto", marginTop: 50 }}>
    <Navbar />
    <Typography variant="h4" gutterBottom>
      Recepción CEDIS
    </Typography>

    <Typography variant="h6" gutterBottom>
      Camiones disponibles
    </Typography>
    <div style={{ height: 200, width: "100%" }}>
      <DataGrid
        rows={camiones.map((c, i) => ({
          id: i,
          numeroCamion: c.numeroCamion
        }))}
        columns={[{ field: "numeroCamion", headerName: "Camión", flex: 1 }]}
        pageSize={5}
        onRowClick={(params) => setCamionSeleccionado(params.row.numeroCamion)}
      />
    </div>

    {camionSeleccionado && (
      <div style={{ marginTop: 20 }}>
        <Typography variant="subtitle1">
          Camión seleccionado: {camionSeleccionado}
        </Typography>
        <Button
          variant="contained"
          color="primary"
          onClick={cargarCamionCompleto}
          style={{ marginRight: 10 }}
        >
          Auditar camión completo
        </Button>

        {["FRUTAS","VERDURAS","LACTEOS","CARNES","FARMACIA"].map(dep => (
          <Button
            key={dep}
            variant="contained"
            color="secondary"
            onClick={() => cargarPorDepartamento(dep)}
            style={{ marginLeft: 10 }}
          >
            Auditar {dep}
          </Button>
        ))}
      </div>
    )}

    {mensaje && (
      <Alert
        severity={mensaje.includes("Error") ? "error" : "info"}
        style={{ marginTop: 20 }}
      >
        {mensaje}
      </Alert>
    )}
    {productos.length > 0 && (
      <div style={{ marginTop: 30 }}>
        <Typography variant="h6" gutterBottom>
          Productos esperados
        </Typography>

        <div style={{ height: 400, width: "100%" }}>
          <DataGrid
           rows={productos}
            columns={[
              { field: "descripcion", headerName: "Producto", flex: 1 },
              { field: "cantidadEsperada", headerName: "Esperado", width: 120 },
              {
                field: "cantidadRecibida",
                headerName: "Recibido",
                width: 120,
                renderCell: (params) => (
                  <TextField
                    type="number"
                    value={params.row.cantidadRecibida}
                    onChange={(e) =>
                      actualizarDetalle(params.row.id, { cantidadRecibida: e.target.value })
                    }
                    size="small"
                  />
                )
              },
              {
                field: "lote",
                headerName: "Lote",
                width: 150,
                renderCell: (params) => (
                  <TextField
                    value={params.row.lote}
                    onChange={(e) =>
                      actualizarDetalle(params.row.id, { lote: e.target.value })
                    }
                    size="small"
                  />
                )
              },
              {
                field: "fechaCaducidad",
                headerName: "Caducidad",
                width: 180,
                renderCell: (params) => (
                  <DatePicker
                    value={params.row.fechaCaducidad}
                    onChange={(newValue) =>
                      actualizarDetalle(params.row.id, { fechaCaducidad: newValue })
                    }
                  />
                )
              },
              { field: "rpcCantidad", headerName: "RPC Cantidad", width: 100,
                  renderCell: (params) => (
                    <TextField
                      type="number"
                      value={params.row.rpcCantidad}
                      onChange={(e) =>
                        actualizarDetalle(params.row.id, { rpcCantidad: e.target.value })
                      }
                      size="small"
                    />
                  )
                },
                {
  field: "rpcTipo",
  headerName: "RPC Tipo",
  width: 150, // Un poco más de ancho para que quepa bien el texto
  renderCell: (params) => (
    <Select
      value={params.row.rpcTipo || ""}
      onChange={(e) =>
        actualizarDetalle(params.row.id, { rpcTipo: e.target.value })
      }
      size="small"
      fullWidth
    >
      <MenuItem value="">—</MenuItem>
      {/* El value debe ser IGUAL al Enum de Java */}
      <MenuItem value="TIPO1">TIPO 1</MenuItem>
      <MenuItem value="TIPO2">TIPO 2</MenuItem>
      <MenuItem value="TIPO3">TIPO 3</MenuItem>
      <MenuItem value="CAJA_CARTON">Caja de Cartón</MenuItem>
      <MenuItem value="OTRO">Otro</MenuItem>
    </Select>
  )
}
            ]}
            pageSize={5}
          />
        </div>

        {/* Botones de acción */}
        <div style={{ marginTop: 20, textAlign: "center" }}>
          <Button
            variant="contained"
            color="success"
            onClick={finalizarRecepcion}
            style={{ marginRight: 10 }}
            disabled={productos.length === 0}
          >
            Finalizar recepción
          </Button>

          <Button
            variant="contained"
            color="warning"
            onClick={cerrarRecepcion}
            disabled={productos.length === 0}
          >
            Cerrar recepción
          </Button>
        </div>
      </div>
    )}

    {rpc.length > 0 && (
      <div style={{ marginTop: 30 }}>
        <Typography variant="h6" gutterBottom>RPC registradas</Typography>
        <DataGrid
          rows={rpc.map(r => ({
            id: r.idRpc,
            tipo: r.tipoRpc,
            cantidadEntregada: r.cantidadEntregada,
            cantidadRetornada: r.cantidadRetornada,
            pendienteRetorno: r.pendienteRetorno ? "Sí" : "No"
          }))}
          columns={[
            { field: "tipo", headerName: "Tipo RPC", flex: 1,
              renderCell: (params) => {
                switch(params.value) {
                  case 1: return "Tipo 1";
                  case 2: return "Tipo 2";
                  case 3: return "Tipo 3";
                  default: return params.value;
                }
              }
            },
            { field: "cantidadEntregada", headerName: "Entregada", width: 120 },
            { field: "cantidadRetornada", headerName: "Retornada", width: 120 },
            { field: "pendienteRetorno", headerName: "Pendiente Retorno", width: 150 }
          ]}
          pageSize={5}
        />
      </div>
    )}

    {discrepancias.length > 0 && (
      <div style={{ marginTop: 30 }}>
        <Typography variant="h6" gutterBottom>Discrepancias detectadas</Typography>
        <Discrepancias discrepancias={discrepancias} />
      </div>
    )}
  </div>
);
}

export default RecepcionCedis;