import React, { useState } from "react";
import { TextField, Button, Typography, Paper } from "@mui/material";
import { useNavigate } from "react-router-dom";

function Login() {
  const [usuario, setUsuario] = useState("");
  const [password, setPassword] = useState("");
  const [tienda, setTienda] = useState("");
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();

    // 🔹 Guardar flag de login simulado
    localStorage.setItem("isLoggedIn", "true");

    // 🔹 Guardar datos opcionales del usuario/tienda
    localStorage.setItem("usuario", usuario);
    localStorage.setItem("tienda", tienda);

    // 🔹 Redirigir al Dashboard
    navigate("/dashboard");
  };

  return (
    <div style={{ display: "flex", justifyContent: "center", alignItems: "center", minHeight: "100vh" }}>
      <Paper elevation={3} style={{ padding: 30, width: 400 }}>
        <Typography variant="h5" gutterBottom>
          Login Demo
        </Typography>
        <form onSubmit={handleSubmit}>
          <TextField
            label="Usuario"
            fullWidth
            margin="normal"
            value={usuario}
            onChange={(e) => setUsuario(e.target.value)}
          />
          <TextField
            label="Password"
            type="password"
            fullWidth
            margin="normal"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <TextField
            label="Tienda"
            fullWidth
            margin="normal"
            value={tienda}
            onChange={(e) => setTienda(e.target.value)}
          />
          <Button type="submit" variant="contained" color="primary" fullWidth>
            Entrar
          </Button>
        </form>
      </Paper>
    </div>
  );
}

export default Login;