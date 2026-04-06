// api/AlertasInventario.jsx
import axios from "axios";

const API_URL = "http://localhost:8080/api/inventario/alertas";

// Función para obtener alertas de inventario
export const getAlertasInventario = async () => {
  try {
    const response = await axios.get(API_URL);
    return response.data;
  } catch (error) {
    console.error("Error al obtener alertas de inventario:", error);
    throw error;
  }
};