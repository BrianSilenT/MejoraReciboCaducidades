
# 📘 Backend Perecederos – API REST
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/BrianSilenT/MejoraReciboCaducidades)
[![Mintlify](https://img.shields.io/badge/Docs-Mintlify-blue)](https://briansilent-mejorarecibocaducidades-79.mintlify.app/)

Sistema integral para la gestión de productos perecederos, control de inventarios, recepción en CEDIS y logística de transporte (RPC). Esta API centraliza la lógica de negocio para reducir mermas y optimizar tiempos de operación.

---

## 🚀 Stack Tecnológico
* **Lenguaje:** Java 17
* **Framework:** Spring Boot 3.5.10
* **Persistencia:** Spring Data JPA / Hibernate
* **Base de Datos:** PostgreSQL
* **Contenerización:** Docker & Docker Compose

---

## 🌐 Infraestructura y Red (Docker)
Este backend está diseñado para ejecutarse en contenedores Docker, comunicándose con una base de datos PostgreSQL alojada en el host (laptop).

### Configuración del Túnel:
* **Host:** `host.docker.internal`
* **Puerto:** `5432`
* **Network:** El `docker-compose.yml` utiliza `extra_hosts` para permitir que el contenedor "salte" hacia el servicio de base de datos de Windows.

Para desplegar el entorno completo:
```bash
docker-compose up --build -d
```

---

## 🔹 Endpoints Principales (Base URL: `/api`)

### 📦 Gestión de Productos
- `POST /producto` → Registrar nuevo producto.
- `GET /producto` → Listado maestro de productos.

### 📥 Recepciones CEDIS (Logística de Entrada)
- `GET /recepciones/cedis/camion/{numeroCamion}` → Consultar estado de carga y discrepancias por transporte.
- `POST /recepciones/cedis` → Registrar recepción física con validación de caducidades.
- `GET /recepciones/cedis/departamento/{departamento}` → Filtrar por área operativa.

### 📦 Inventario e Inteligencia de Caducidad
- `GET /inventario/alertas` → **Algoritmo de alertas** automáticas para productos próximos a vencer.
- `GET /inventario/caducados?limite=YYYY-MM-DD` → Listar mermas para depuración de stock.
- `GET /inventario/buscar?codigoBarras=...` → Búsqueda rápida por scanner.

### 🚚 Módulo RPC (Recibo / Préstamo / Cambio)
- `POST /rpc/entrega` → Registrar salida de mercancía en camión.
- `PUT /rpc/retorno/{idRpc}` → Registrar cierre de ciclo y cantidades retornadas.
- `GET /rpc/pendientes` → Auditoría de folios sin completar.

### 📊 Análisis de Datos
- `GET /dashboard` → Métricas en tiempo real de inventario, recepciones del día y estatus RPC.

---

## 📝 Integración Frontend
El sistema está optimizado para ser consumido por un cliente **React/Vite**.
- **Dashboard:** Consume `/api/dashboard` para KPIs globales.
- **Inventario:** Consume `/api/inventario/alertas` para gestión proactiva.
- **Logística:** Consume `/api/recepciones/cedis` para validación de transporte.

---

## 📖 Documentación Extendida
- **Análisis de Negocio:** [DeepWiki - Mejora Recibo Caducidades](https://deepwiki.com/BrianSilenT/MejoraReciboCaducidades)
- **Referencia Técnica de API:** [Mintlify Docs](https://briansilent-mejorarecibocaducidades-79.mintlify.app/)

---
**Desarrollado por [BrianSilenT](https://github.com/BrianSilenT)**
```
