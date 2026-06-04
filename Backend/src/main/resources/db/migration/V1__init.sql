-- =========================
-- PRODUCTO
-- =========================
CREATE TABLE producto (
    id_producto BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    codigo_barras VARCHAR(100) UNIQUE NOT NULL,
    precio DOUBLE PRECISION,
    departamento VARCHAR(50),
    division VARCHAR(50),
    descripcion TEXT
);

-- =========================
-- PRODUCTO ALIAS
-- =========================
CREATE TABLE producto_alias (
    id BIGSERIAL PRIMARY KEY,
    codigo_venta VARCHAR(100),
    codigo_inventario VARCHAR(100),
    activo BOOLEAN DEFAULT TRUE
);

-- =========================
-- ORDEN COMPRA
-- =========================
CREATE TABLE orden_compra (
    id_orden BIGSERIAL PRIMARY KEY,
    fecha_emision DATE,
    vigencia_dias INTEGER,
    estado VARCHAR(50)
);

CREATE TABLE orden_compra_detalle (
    id_detalle BIGSERIAL PRIMARY KEY,
    id_orden BIGINT,
    id_producto BIGINT,
    cantidad_recibida NUMERIC(12,3) DEFAULT 0,
    cantidad_esperada NUMERIC(12,3),

    CONSTRAINT fk_oc FOREIGN KEY (id_orden) REFERENCES orden_compra(id_orden),
    CONSTRAINT fk_oc_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

-- =========================
-- RECEPCION NORMAL
-- =========================
CREATE TABLE recepcion (
    id_recepcion BIGSERIAL PRIMARY KEY,
    fecha_recepcion DATE NOT NULL,
    estado VARCHAR(50),
    id_orden BIGINT,
    usuario VARCHAR(100),

    CONSTRAINT fk_recepcion_orden FOREIGN KEY (id_orden) REFERENCES orden_compra(id_orden)
);

CREATE TABLE recepcion_detalle (
    id_detalle BIGSERIAL PRIMARY KEY,
    id_recepcion BIGINT,
    id_producto BIGINT,
    orden_compra_detalle_id BIGINT,
    cantidad_recibida NUMERIC(12,3),
    lote VARCHAR(100),
    fecha_caducidad DATE,

    CONSTRAINT fk_rec FOREIGN KEY (id_recepcion) REFERENCES recepcion(id_recepcion),
    CONSTRAINT fk_rec_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto),
    CONSTRAINT fk_rec_oc_det FOREIGN KEY (orden_compra_detalle_id) REFERENCES orden_compra_detalle(id_detalle)
);

-- =========================
-- RECEPCION CEDIS
-- =========================
CREATE TABLE recepcion_cedis (
    id_recepcion_cedis BIGSERIAL PRIMARY KEY,
    numero_camion VARCHAR(100),
    fecha_recepcion DATE,
    estado VARCHAR(50),
    division VARCHAR(50),
    departamento VARCHAR(50),
    total_esperado NUMERIC(12,3),
    total_recibido NUMERIC(12,3),
    porcentaje_auditado DOUBLE PRECISION,
    completa BOOLEAN
);

CREATE TABLE recepcion_cedis_detalle (
    id_detalle_cedis BIGSERIAL PRIMARY KEY,
    id_recepcion_cedis BIGINT,
    id_producto BIGINT,
    cantidad_esperada NUMERIC(12,3),
    cantidad_recibida NUMERIC(12,3),
    lote VARCHAR(100),
    fecha_caducidad DATE,
    cantidad_rpc INTEGER,
    tipo_rpc VARCHAR(50),

    CONSTRAINT fk_cedis FOREIGN KEY (id_recepcion_cedis) REFERENCES recepcion_cedis(id_recepcion_cedis),
    CONSTRAINT fk_cedis_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

-- =========================
-- INVENTARIO
-- =========================
CREATE TABLE inventario (
    id_inventario BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    cantidad NUMERIC(12,3),
    fecha_caducidad DATE,
    lote VARCHAR(100),
    fecha_llegada DATE,
    ubicacion VARCHAR(50),
    division VARCHAR(50),
    departamento VARCHAR(50),

    CONSTRAINT fk_inv_producto FOREIGN KEY (producto_id) REFERENCES producto(id_producto)
);

-- =========================
-- MOVIMIENTO INVENTARIO
-- =========================
CREATE TABLE movimiento_inventario (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    lote VARCHAR(100),
    cantidad NUMERIC(12,3),
    tipo_movimiento VARCHAR(50),
    ubicacion_origen VARCHAR(50),
    ubicacion_destino VARCHAR(50),
    referencia VARCHAR(100),
    usuario VARCHAR(100),
    motivo VARCHAR(255),
    fecha TIMESTAMP,
    fecha_caducidad DATE,

    CONSTRAINT fk_mov_producto FOREIGN KEY (producto_id) REFERENCES producto(id_producto)
);

-- =========================
-- DISCREPANCIA
-- =========================
CREATE TABLE discrepancia_recepcion (
    id_discrepancia BIGSERIAL PRIMARY KEY,
    numero_camion VARCHAR(100),
    departamento VARCHAR(50),
    total_esperado NUMERIC(12,3),
    total_recibido NUMERIC(12,3),
    total_faltante NUMERIC(12,3),
    fecha_registro DATE
);

-- =========================
-- RPC CONTROL
-- =========================
CREATE TABLE rpc_control (
    id_rpc BIGSERIAL PRIMARY KEY,
    numero_camion VARCHAR(100) NOT NULL,
    id_recepcion_cedis BIGINT,
    departamento VARCHAR(50),
    tipo_rpc VARCHAR(50),
    cantidad_entregada INTEGER,
    cantidad_retornada INTEGER,
    fecha_registro DATE,
    pendiente_retorno BOOLEAN,

    CONSTRAINT fk_rpc_cedis FOREIGN KEY (id_recepcion_cedis) REFERENCES recepcion_cedis(id_recepcion_cedis)
);

-- =========================
-- AUDITORIA SURTIDO
-- =========================
CREATE TABLE auditoria_surtido (
    id BIGSERIAL PRIMARY KEY,
    upc VARCHAR(100),
    lote VARCHAR(100),
    cantidad_sugerida NUMERIC(12,3),
    cantidad_surtida NUMERIC(12,3),
    correcto BOOLEAN,
    motivo VARCHAR(255),
    fecha TIMESTAMP
);

-- =========================
-- SUGERENCIA SURTIDO
-- =========================
CREATE TABLE sugerencia_surtido (
    id BIGSERIAL PRIMARY KEY,
    upc VARCHAR(100),
    lote VARCHAR(100),
    cantidad_sugerida NUMERIC(12,3),
    fecha_caducidad DATE,
    fecha_generacion TIMESTAMP
);

-- =========================
-- USUARIO
-- =========================
CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50),
    activo BOOLEAN
);