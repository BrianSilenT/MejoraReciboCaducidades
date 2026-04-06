import React, { useState, useEffect } from "react";
import { getInventarioPorCodigo } from "../api/Inventario";

function InventarioTest() {
  const [producto, setProducto] = useState(null);

  useEffect(() => {
    getInventarioPorCodigo("7501234567890").then(data => setProducto(data));
  }, []);

  return (
    <div>
      {producto ? (
        <p>{producto.descripcion} - {producto.cantidad} unidades</p>
      ) : (
        <p>Cargando producto...</p>
      )}
    </div>
  );
}

export default InventarioTest;
