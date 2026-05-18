-- ============================================================
-- BARBA ROJA TURNOS - CONSULTAS PARA INFORME TP2 / AP2
-- 5 consultas representativas del sistema de gestión de turnos
-- ============================================================

USE barba_roja_turnos;

-- ============================================================
-- 1. AGENDA COMPLETA DEL DÍA
--    Muestra todos los turnos de una jornada con cliente,
--    barbero, servicio, sucursal y estado.
-- ============================================================

SELECT
    t.hora_inicio AS hora,
    t.hora_fin,
    CONCAT(c.nombre, ' ', c.apellido) AS cliente,
    sv.nombre AS servicio,
    sv.precio,
    CONCAT(b.nombre, ' ', b.apellido) AS barbero,
    s.nombre AS sucursal,
    t.estado
FROM turno t
INNER JOIN cliente c  ON t.id_cliente  = c.id_cliente
INNER JOIN servicio sv ON t.id_servicio = sv.id_servicio
INNER JOIN barbero b  ON t.id_barbero  = b.id_barbero
INNER JOIN sucursal s ON t.id_sucursal = s.id_sucursal
WHERE t.fecha = '2025-05-23'
ORDER BY s.nombre, t.hora_inicio;

-- ============================================================
-- 2. DASHBOARD DE RESUMEN OPERATIVO
--    Totales del sistema en una sola fila: turnos del día,
--    clientes registrados, barberos activos y sucursales.
-- ============================================================

SELECT
    (SELECT COUNT(*) FROM turno     WHERE fecha = '2025-05-23') AS turnos_del_dia,
    (SELECT COUNT(*) FROM cliente)                               AS clientes_registrados,
    (SELECT COUNT(*) FROM barbero   WHERE estado = 'ACTIVO')    AS barberos_activos,
    (SELECT COUNT(*) FROM sucursal  WHERE estado = 'ACTIVA')    AS sucursales_activas;

-- ============================================================
-- 3. REPORTE DE INGRESOS ESTIMADOS POR DÍA
--    Cantidad de turnos y recaudación estimada agrupada
--    por fecha, excluyendo turnos cancelados.
-- ============================================================

SELECT
    t.fecha,
    COUNT(t.id_turno)  AS cantidad_turnos,
    SUM(sv.precio)     AS ingresos_estimados
FROM turno t
INNER JOIN servicio sv ON t.id_servicio = sv.id_servicio
WHERE t.estado <> 'CANCELADO'
GROUP BY t.fecha
ORDER BY t.fecha;

-- ============================================================
-- 4. RANKING DE BARBEROS POR CANTIDAD DE TURNOS
--    Permite identificar los barberos más demandados
--    y su sucursal de pertenencia.
-- ============================================================

SELECT
    CONCAT(b.nombre, ' ', b.apellido) AS barbero,
    b.especialidad,
    s.nombre                           AS sucursal,
    COUNT(t.id_turno)                  AS turnos_asignados
FROM barbero b
INNER JOIN sucursal s ON b.id_sucursal = s.id_sucursal
LEFT  JOIN turno t    ON b.id_barbero  = t.id_barbero
GROUP BY b.id_barbero, b.nombre, b.apellido, b.especialidad, s.nombre
ORDER BY turnos_asignados DESC;

-- ============================================================
-- 5. HISTORIAL DE TURNOS POR CLIENTE
--    Muestra cuántas veces fue cada cliente y cuál fue
--    su última visita. Útil para fidelización.
-- ============================================================

SELECT
    CONCAT(c.nombre, ' ', c.apellido) AS cliente,
    c.telefono,
    c.email,
    COUNT(t.id_turno)  AS visitas_totales,
    MAX(t.fecha)       AS ultima_visita
FROM cliente c
LEFT JOIN turno t ON c.id_cliente = t.id_cliente
GROUP BY c.id_cliente, c.nombre, c.apellido, c.telefono, c.email
ORDER BY visitas_totales DESC;

-- ============================================================
-- FIN
-- ============================================================
