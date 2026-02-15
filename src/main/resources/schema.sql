-- Tabla de productos
CREATE TABLE producto (
    id_producto BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_barras VARCHAR(20) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    nombre VARCHAR(255),
    categoria VARCHAR(100),
    presentacion VARCHAR(100),
    proveedor VARCHAR(100),
    departamento VARCHAR(50),
    division VARCHAR(50)
);

-- Órdenes de compra
CREATE TABLE orden_compra (
    id_orden BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_orden VARCHAR(50) NOT NULL,
    fecha_emision DATE,
    vigencia_dias INT,
    cantidad_solicitada INT,
    proveedor VARCHAR(100),
    estado VARCHAR(50),
    departamento VARCHAR(50),
    division VARCHAR(50)
);

-- Recepciones de proveedor
CREATE TABLE recepcion (
    id_recepcion BIGINT AUTO_INCREMENT PRIMARY KEY,
    lote VARCHAR(50),
    cantidad INT,
    fecha_recepcion DATE,
    fecha_caducidad DATE,
    estado VARCHAR(50),
    id_orden BIGINT,
    id_producto BIGINT,
    CONSTRAINT fk_recepcion_orden FOREIGN KEY (id_orden) REFERENCES orden_compra(id_orden),
    CONSTRAINT fk_recepcion_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

-- Recepciones en CEDIS
CREATE TABLE recepcion_cedis (
    id_recepcion_cedis BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_camion VARCHAR(50),
    fecha_recepcion DATE,
    division VARCHAR(50),
    departamento VARCHAR(50),
    total_esperado INT,
    total_recibido INT,
    porcentaje_auditado DECIMAL(5,2),
    completa BOOLEAN
);

-- Inventario
CREATE TABLE inventario (
    id_inventario BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_barras VARCHAR(20),
    descripcion VARCHAR(255),
    cantidad INT,
    fecha_caducidad DATE,
    lote VARCHAR(50),
    fecha_llegada DATE,
    division VARCHAR(50),
    departamento VARCHAR(50),
    CONSTRAINT fk_inventario_producto FOREIGN KEY (codigo_barras) REFERENCES producto(codigo_barras)
);
CREATE TABLE rpc_control (
    id_rpc BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_camion VARCHAR(50) NOT NULL,
    departamento VARCHAR(50),   -- Enum: FRUTAS, VERDURAS, etc.
    cantidad_entregada INT CHECK (cantidad_entregada >= 1),
    cantidad_retornada INT CHECK (cantidad_retornada >= 0),
    fecha_registro DATE,
    pendiente_retorno BOOLEAN
);