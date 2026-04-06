import React, { useState } from "react";
import { AppBar, Toolbar, IconButton, Typography, Drawer, List, ListItem, ListItemButton, ListItemText, Box } from "@mui/material";
import MenuIcon from "@mui/icons-material/Menu";
import { useNavigate } from "react-router-dom";

function Navbar() {
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();

  const toggleDrawer = () => setOpen(!open);

  // NOTA: Estos paths deben ser EXACTAMENTE iguales a los de App.js
  const menuItems = [
    { text: "Dashboard", path: "/dashboard" },
    { text: "Inventario", path: "/inventario" },
    { text: "Recepción Proveedores", path: "/recepcion" }, 
    { text: "Recepción CEDIS", path: "/recepcion-cedis" },
    { text: "Control RPC", path: "/rpc" } 
  ];

  return (
    <>
      <AppBar position="static" sx={{ bgcolor: '#0071ce' }}> {/* Color corporativo opcional */}
        <Toolbar>
          <IconButton edge="start" color="inherit" onClick={toggleDrawer} sx={{ mr: 2 }}>
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Sistema Bodega Aurrera
          </Typography>
        </Toolbar>
      </AppBar>

      <Drawer anchor="left" open={open} onClose={toggleDrawer}>
        <Box sx={{ width: 250 }} role="presentation">
          <List>
            {menuItems.map((item) => (
              <ListItem key={item.text} disablePadding>
                <ListItemButton onClick={() => { navigate(item.path); toggleDrawer(); }}>
                  <ListItemText primary={item.text} />
                </ListItemButton>
              </ListItem>
            ))}
          </List>
        </Box>
      </Drawer>
    </>
  );
}

export default Navbar;