# 🛒 Perecederos Demo - Backend

Proyecto de backend en **Spring Boot** para la gestión de inventario, recepciones y alertas de caducidad en piso de ventas.

---

## 🚀 Instalación

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/<tu-usuario>/<tu-repo>.git
   cd perecederos_demo
   ```

2. Compilar y ejecutar con Maven:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

3. La aplicación se levantará en:
   ```
   http://localhost:8080
   ```

---

## 📌 Endpoints principales

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/inventario/{codigoBarras}` | GET | Consulta inventario por código de barras |
| `/api/inventario/por-caducar/{dias}` | GET | Lista productos próximos a caducar |
| `/api/inventario/alertas` | GET | Genera alertas de caducidad |
| `/api/inventario/control-caducidad` | GET | Tabla ordenada por fecha de caducidad |
| `/api/inventario/division/{division}` | GET | Filtra inventario por división |
| `/api/inventario/departamento/{departamento}` | GET | Filtra inventario por departamento |
| `/api/recepcion/cedis` | POST | Registrar recepción desde CEDIS |
| `/api/discrepancias` | GET | Listar discrepancias en recepciones |

---

## 🧪 Pruebas con Postman

- Se incluye una **colección Postman** en el repo (`postman_collection.json`).  
- Importa la colección en Postman para probar todos los endpoints.  
- Cada request tiene ejemplos de body y respuesta esperada.

---

## 📖 Documentación con Swagger/OpenAPI

Si Swagger/OpenAPI está habilitado en tu proyecto:

- **Swagger UI interactivo**:  
  ```
  http://localhost:8080/swagger-ui.html
  ```

- **OpenAPI JSON**:  
  ```
  http://localhost:8080/v3/api-docs
  ```

> ⚠️ Nota: si Swagger no logra cargar algunos modelos, usa la colección Postman como documentación principal.

---

## 🗂️ Organización del proyecto

- `Controller/` → Endpoints REST.  
- `Service/` → Lógica de negocio.  
- `Repository/` → Acceso a datos (JPA).  
- `Model/` → Entidades y DTOs.  
- `resources/data.sql` → Datos iniciales de prueba.  

---

## 📌 Roadmap

- [x] Backend con Spring Boot.  
- [x] Endpoints de inventario, recepciones y discrepancias.  
- [x] Pruebas en Postman.  
- [ ] Frontend en React (pendiente).  
- [ ] Integración completa con Swagger/OpenAPI.  

---

## 👨‍💻 Autor
**Brian Plasencia Guzmán**  
Tech Lead & Backend Architect
```

---

