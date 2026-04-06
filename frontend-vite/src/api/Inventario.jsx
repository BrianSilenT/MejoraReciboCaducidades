import axios from "axios";

export const getInventarioPorCodigo = async (codigoBarras) => {
  const res = await axios.get(`/api/Inventario/${codigoBarras}`);
  return res.data;
};