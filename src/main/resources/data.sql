-- Productos base
INSERT INTO producto (codigo_barras, descripcion) VALUES ('7501111111111', 'Paracetamol 500mg');
INSERT INTO producto (codigo_barras, descripcion) VALUES ('7502222222222', 'Pechuga de pollo fresca');
INSERT INTO producto (codigo_barras, descripcion) VALUES ('7503333333333', 'Salchicha de pavo');
INSERT INTO producto (codigo_barras, descripcion) VALUES ('7504444444444', 'Leche entera 1L');

-- Órdenes de compra (usando campos reales)
INSERT INTO orden_compra (numero_orden, fecha_emision, vigencia_dias, cantidad_solicitada, proveedor, estado)
VALUES ('OC-001', '2026-01-25', 10, 100, 'Farmacia SA', 'EXPIRADA');

INSERT INTO orden_compra (numero_orden, fecha_emision, vigencia_dias, cantidad_solicitada, proveedor, estado)
VALUES ('OC-002', '2026-02-05', 10, 50, 'Perecederos SA', 'VIGENTE');

INSERT INTO orden_compra (numero_orden, fecha_emision, vigencia_dias, cantidad_solicitada, proveedor, estado)
VALUES ('OC-003', '2026-02-01', 10, 10, 'Embutidos SA', 'VIGENTE');

INSERT INTO orden_compra (numero_orden, fecha_emision, vigencia_dias, cantidad_solicitada, proveedor, estado)
VALUES ('OC-004', '2026-02-07', 10, 120, 'Lacteos SA', 'VIGENTE');

-- Inventario inicial
INSERT INTO inventario (codigo_barras, descripcion, cantidad, fecha_caducidad, lote)
VALUES ('7501111111111', 'Paracetamol 500mg', 50, '2026-02-05', 'FAR-001'); -- Vencido

INSERT INTO inventario (codigo_barras, descripcion, cantidad, fecha_caducidad, lote)
VALUES ('7502222222222', 'Pechuga de pollo fresca', 30, '2026-02-10', 'PER-045'); -- Próximo a caducar

INSERT INTO inventario (codigo_barras, descripcion, cantidad, fecha_caducidad, lote)
VALUES ('7503333333333', 'Salchicha de pavo', 40, '2026-02-25', 'EMB-010'); -- Vigente

INSERT INTO inventario (codigo_barras, descripcion, cantidad, fecha_caducidad, lote)
VALUES ('7504444444444', 'Leche entera 1L', 60, '2026-02-12', 'LAC-007'); -- Próximo a caducar