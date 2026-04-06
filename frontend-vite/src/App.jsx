import { Routes, Route } from "react-router-dom";
import Login from "@pages/Login";
import Dashboard from "@pages/Dashboard";
import Inventario from "@pages/Inventario";
import InventarioTest from "@components/InventarioTest";
import Recepcion from "@pages/Recepcion";
import RecepcionCedis from "@pages/RecepcionCedis";
import RpcControl from "@pages/RpcControl";
import InventarioDetalle from "./components/InventarioDetalle";
import PrivateRoute from "./components/PrivateRoute";
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';

function App() {
  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <Routes>
        {/* Pantalla inicial → Login */}
        <Route path="/" element={<Login />} />

        {/* Dashboard y demás rutas protegidas */}
        <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
        <Route path="/inventario" element={<PrivateRoute><Inventario /></PrivateRoute>} />
        <Route path="/inventario-test" element={<PrivateRoute><InventarioTest /></PrivateRoute>} />
        <Route path="/recepcion" element={<PrivateRoute><Recepcion /></PrivateRoute>} />
        <Route path="/recepcion-cedis" element={<PrivateRoute><RecepcionCedis /></PrivateRoute>} />
        <Route path="/rpc" element={<PrivateRoute><RpcControl /></PrivateRoute>} />
        <Route path="/inventario/:codigoBarras" element={<PrivateRoute><InventarioDetalle /></PrivateRoute>} />
      </Routes>
    </LocalizationProvider>
  );
}

export default App;