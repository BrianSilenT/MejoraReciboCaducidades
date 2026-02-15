-- Productos base
INSERT INTO producto (codigo_barras, descripcion, departamento, division, nombre, categoria, presentacion, proveedor)
VALUES ('7501111111111', 'Paracetamol 500mg', 'FARMACIA', 'NO_PERECEDEROS', 'Paracetamol', 'Medicamento', 'Caja 20 tabletas', 'Farmacia SA');

INSERT INTO producto (codigo_barras, descripcion, departamento, division, nombre, categoria, presentacion, proveedor)
VALUES ('7502222222222', 'Pechuga de pollo fresca', 'FRUTAS', 'PERECEDEROS', 'Pechuga de pollo', 'Carnes blancas', 'Bandeja 1kg', 'Perecederos SA');

INSERT INTO producto (codigo_barras, descripcion, departamento, division, nombre, categoria, presentacion, proveedor)
VALUES ('7503333333333', 'Salchicha de pavo', 'CARNES', 'PERECEDEROS', 'Salchicha de pavo', 'Embutidos', 'Paquete 500g', 'Embutidos SA');

INSERT INTO producto (codigo_barras, descripcion, departamento, division, nombre, categoria, presentacion, proveedor)
VALUES ('7504444444444', 'Leche entera 1L', 'LACTEOS', 'PERECEDEROS', 'Leche entera', 'Lácteos', 'Botella 1L', 'Lacteos SA');

-- Órdenes de compra
INSERT INTO orden_compra (numero_orden, fecha_emision, vigencia_dias, cantidad_solicitada, proveedor, estado, departamento, division)
VALUES ('OC-001', '2026-01-25', 10, 100, 'Farmacia SA', 'EXPIRADA', 'FARMACIA', 'NO_PERECEDEROS');

INSERT INTO orden_compra (numero_orden, fecha_emision, vigencia_dias, cantidad_solicitada, proveedor, estado, departamento, division)
VALUES ('OC-002', '2026-02-05', 10, 50, 'Perecederos SA', 'VIGENTE', 'FRUTAS', 'PERECEDEROS');

INSERT INTO orden_compra (numero_orden, fecha_emision, vigencia_dias, cantidad_solicitada, proveedor, estado, departamento, division)
VALUES ('OC-003', '2026-02-01', 10, 10, 'Embutidos SA', 'VIGENTE', 'CARNES', 'PERECEDEROS');

INSERT INTO orden_compra (numero_orden, fecha_emision, vigencia_dias, cantidad_solicitada, proveedor, estado, departamento, division)
VALUES ('OC-004', '2026-02-07', 10, 120, 'Lacteos SA', 'VIGENTE', 'LACTEOS', 'PERECEDEROS');

-- Recepciones de proveedor
INSERT INTO recepcion (lote, cantidad, fecha_recepcion, fecha_caducidad, estado, id_orden, id_producto)
VALUES ('FAR-001', 50, '2026-01-30', '2026-02-05', 'ACEPTADA', 1, 1);

INSERT INTO recepcion (lote, cantidad, fecha_recepcion, fecha_caducidad, estado, id_orden, id_producto)
VALUES ('PER-045', 30, '2026-02-05', '2026-02-10', 'ACEPTADA', 2, 2);

-- Recepciones en CEDIS (RPC)
INSERT INTO recepcion_cedis (numero_camion, fecha_recepcion, division, departamento, total_esperado, total_recibido, porcentaje_auditado, completa)
VALUES ('CAM-001', '2026-02-08', 'PERECEDEROS', 'CARNES', 10, 10, 100, true);

INSERT INTO recepcion_cedis (numero_camion, fecha_recepcion, division, departamento, total_esperado, total_recibido, porcentaje_auditado, completa)
VALUES ('CAM-002', '2026-02-09', 'PERECEDEROS', 'LACTEOS', 120, 115, 95.8, false);

-- Inventario inicial
INSERT INTO inventario (codigo_barras, descripcion, cantidad, fecha_caducidad, lote, fecha_llegada, division, departamento)
VALUES ('7501111111111', 'Paracetamol 500mg', 50, '2026-02-05', 'FAR-001', '2026-01-30', 'NO_PERECEDEROS', 'FARMACIA'); -- Vencido

INSERT INTO inventario (codigo_barras, descripcion, cantidad, fecha_caducidad, lote, fecha_llegada, division, departamento)
VALUES ('7502222222222', 'Pechuga de pollo fresca', 30, '2026-02-10', 'PER-045', '2026-02-05', 'PERECEDEROS', 'FRUTAS'); -- Próximo a caducar

INSERT INTO inventario (codigo_barras, descripcion, cantidad, fecha_caducidad, lote, fecha_llegada, division, departamento)
VALUES ('7503333333333', 'Salchicha de pavo', 40, '2026-02-25', 'EMB-010', '2026-02-01', 'PERECEDEROS', 'CARNES'); -- Vigente

INSERT INTO inventario (codigo_barras, descripcion, cantidad, fecha_caducidad, lote, fecha_llegada, division, departamento)
VALUES ('7504444444444', 'Leche entera 1L', 60, '2026-02-12', 'LAC-007', '2026-02-07', 'PERECEDEROS', 'LACTEOS'); -- Próximo a caducar

-- RPC: Camión de frutas entregado completo
INSERT INTO rpc_control (numero_camion, departamento, cantidad_entregada, cantidad_retornada, fecha_registro, pendiente_retorno)
VALUES ('CAM-FRU-001', 'FRUTAS', 100, 0, '2026-02-08', false);

-- RPC: Camión de verduras con retorno parcial
INSERT INTO rpc_control (numero_camion, departamento, cantidad_entregada, cantidad_retornada, fecha_registro, pendiente_retorno)
VALUES ('CAM-VER-002', 'VERDURAS', 80, 10, '2026-02-09', true);