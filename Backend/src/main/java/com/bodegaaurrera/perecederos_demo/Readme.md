# 🧪 PROYECTO PILOTO – CONTROL DE MERMA EN PERECEDEROS

## OBJETIVO

Demostrar que un sistema basado en:

* control por lote
* caducidad
* surtido PEPS (FIFO)

reduce la merma en tienda.

---

## ALCANCE DEL PILOTO

Este sistema:

* NO reemplaza el sistema oficial
* funciona como sistema paralelo
* se alimenta manualmente con:

    * órdenes de compra
    * recepciones
    * inventario inicial

---

## FLUJO SIMPLIFICADO

1. Se registra orden de compra (manual)
2. Se registra recepción (manual)
3. Se genera inventario en BODEGA
4. Se imprimen etiquetas PEPS
5. Se realiza surtido a piso
6. Se registran:

    * ventas
    * mermas

---

## REGLAS CLAVE

* Inventario controlado por:

    * lote
    * caducidad
    * ubicación

* Venta:

    * descuenta automáticamente por FEFO

* Surtido:

    * mueve inventario (no descuenta)

* Merma:

    * descuenta del sistema

---

## MÉTRICA PRINCIPAL

Comparación:

ANTES vs DESPUÉS

* merma total
* merma por producto
* días de vida útil aprovechados

---

## HIPÓTESIS

El uso de:

* etiquetas PEPS
* surtido guiado
* descuento automático por caducidad

reducirá la merma en tienda.

---
