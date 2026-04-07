[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/BrianSilenT/MejoraReciboCaducidades)
https://deepwiki.com/BrianSilenT/MejoraReciboCaducidades
# 📘 Backend Perecederos – API REST
https://briansilent-mejorarecibocaducidades-79.mintlify.app/
Este backend expone endpoints para gestionar productos, órdenes de compra, recepciones, inventario, RPC y dashboard.  
La API responde en formato **JSON** y está pensada para ser consumida desde Postman o cualquier cliente HTTP.



## 🔹 Base URL
```
http://localhost:8080/api
```

---

## 📦 Producto
- `POST /producto` → registrar producto  
- `GET /producto` → listar productos  

---

## 📑 Órdenes de compra
- `GET /ordenes-compra/list` → listar órdenes  
- `POST /ordenes-compra` → registrar orden  
- `GET /ordenes-compra/{id}/vigente` → validar vigencia  
- `PUT /ordenes-compra/{id}/estado` → actualizar estado  

---

## 📥 Recepciones CEDIS
- `POST /recepciones/cedis` → registrar recepción  
- `GET /recepciones/cedis/camion/{numeroCamion}` → recepciones por camión  
- `GET /recepciones/cedis/departamento/{departamento}` → recepciones por departamento  
- `GET /recepciones/cedis/division/{division}` → recepciones por división  

---

## 📦 Inventario
- `GET /inventario` → listar inventario  
- `GET /inventario/caducados?limite=YYYY-MM-DD` → listar caducados  
- `GET /inventario/por-caducar?limite=YYYY-MM-DD` → listar próximos a caducar  
- `GET /inventario/alertas` → generar alertas automáticas  
- `GET /inventario/buscar?codigoBarras=...` → buscar producto por código de barras  
- `GET /inventario/division?division=...` → filtrar por división  
- `GET /inventario/departamento?departamento=...` → filtrar por departamento  

---

## 🚚 RPC
- `POST /rpc/entrega` → registrar entrega  
- `PUT /rpc/retorno/{idRpc}?cantidadRetornada=...` → registrar retorno  
- `GET /rpc/pendientes` → listar pendientes  
- `GET /rpc/completados` → listar completados  
- `GET /rpc/camion/{numeroCamion}` → listar por camión  

---

## 📊 Dashboard
- `GET /dashboard` → resumen de inventario, recepciones y RPC  

---

## 🔹 Notas de uso en frontend
- El **dashboard** consume `/api/dashboard` para mostrar métricas globales.  
- El **módulo de inventario** consume `/api/inventario` y `/api/inventario/alertas`.  
- El **módulo de órdenes de compra** consume `/api/ordenes-compra`.  
- El **módulo de recepciones CEDIS** consume `/api/recepciones/cedis`.  
- El **módulo RPC** consume `/api/rpc` para auditorías de camiones y retornos.  
