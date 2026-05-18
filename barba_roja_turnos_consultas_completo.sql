-- ============================================================
-- BARBA ROJA TURNOS - SCRIPT COMPLETO MYSQL PARA TP2 / AP2
-- Sistema de gestión de turnos y agenda operativa para barbería multisucursal
-- Incluye: creación de base de datos, tablas, inserciones, consultas,
-- actualización, borrado y reportes.
-- ============================================================

-- ============================================================
-- 1. CREACIÓN DE BASE DE DATOS
-- ============================================================

CREATE DATABASE IF NOT EXISTS barba_roja_turnos
CHARACTER SET utf8mb4
COLLATE utf8mb4_spanish_ci;

USE barba_roja_turnos;

-- ============================================================
-- 2. LIMPIEZA DE TABLAS SI YA EXISTEN
-- ============================================================

DROP TABLE IF EXISTS turno;
DROP TABLE IF EXISTS horario_barbero;
DROP TABLE IF EXISTS barbero;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS servicio;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS sucursal;

-- ============================================================
-- 3. CREACIÓN DE TABLAS
-- ============================================================

CREATE TABLE sucursal (
    id_sucursal INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(150) NOT NULL,
    telefono VARCHAR(30),
    estado ENUM('ACTIVA', 'INACTIVA') NOT NULL DEFAULT 'ACTIVA'
);

CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol ENUM('ADMINISTRADOR', 'RECEPCIONISTA', 'BARBERO') NOT NULL,
    estado ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    id_sucursal INT,
    FOREIGN KEY (id_sucursal) REFERENCES sucursal(id_sucursal)
);

CREATE TABLE cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    telefono VARCHAR(30) NOT NULL,
    email VARCHAR(120),
    observaciones VARCHAR(255)
);

CREATE TABLE servicio (
    id_servicio INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    duracion_min INT NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    estado ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO'
);

CREATE TABLE barbero (
    id_barbero INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    especialidad VARCHAR(120),
    telefono VARCHAR(30),
    estado ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    id_sucursal INT NOT NULL,
    FOREIGN KEY (id_sucursal) REFERENCES sucursal(id_sucursal)
);

CREATE TABLE horario_barbero (
    id_horario INT AUTO_INCREMENT PRIMARY KEY,
    id_barbero INT NOT NULL,
    dia_semana ENUM('LUNES','MARTES','MIERCOLES','JUEVES','VIERNES','SABADO','DOMINGO') NOT NULL,
    hora_desde TIME NOT NULL,
    hora_hasta TIME NOT NULL,
    trabaja BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id_barbero) REFERENCES barbero(id_barbero)
);

CREATE TABLE turno (
    id_turno INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_barbero INT NOT NULL,
    id_servicio INT NOT NULL,
    id_sucursal INT NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado ENUM('PROGRAMADO', 'CONFIRMADO', 'EN_CURSO', 'COMPLETADO', 'CANCELADO') NOT NULL DEFAULT 'PROGRAMADO',
    observaciones VARCHAR(255),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    FOREIGN KEY (id_barbero) REFERENCES barbero(id_barbero),
    FOREIGN KEY (id_servicio) REFERENCES servicio(id_servicio),
    FOREIGN KEY (id_sucursal) REFERENCES sucursal(id_sucursal)
);

-- ============================================================
-- 4. INSERCIÓN DE DATOS DE PRUEBA
-- ============================================================

INSERT INTO sucursal (nombre, direccion, telefono, estado) VALUES
('Centro', 'Av. Principal 123', '3704-111111', 'ACTIVA'),
('Norte', 'Calle Norte 456', '3704-222222', 'ACTIVA'),
('Sur', 'Av. Sur 789', '3704-333333', 'ACTIVA');

INSERT INTO usuario (username, password_hash, rol, estado, id_sucursal) VALUES
('admin', 'hash_admin_123', 'ADMINISTRADOR', 'ACTIVO', 1),
('recepcion_centro', 'hash_recepcion_123', 'RECEPCIONISTA', 'ACTIVO', 1),
('barbero_juan', 'hash_barbero_123', 'BARBERO', 'ACTIVO', 1);

INSERT INTO cliente (nombre, apellido, telefono, email, observaciones) VALUES
('Martín', 'López', '3704-555001', 'martinlopez@mail.com', 'Cliente frecuente'),
('Emiliano', 'García', '3704-555002', 'emilianogarcia@mail.com', 'Prefiere turno por la mañana'),
('Luciano', 'Fernández', '3704-555003', 'lucianofernandez@mail.com', 'Solicita degradado medio'),
('Diego', 'Álvarez', '3704-555004', 'diegoalvarez@mail.com', 'Sin observaciones'),
('Santiago', 'Morales', '3704-555005', 'santiagomorales@mail.com', 'Cliente nuevo'),
('Matías', 'Rojas', '3704-555006', 'matiasrojas@mail.com', 'Prefiere atención con Luis');

INSERT INTO servicio (nombre, duracion_min, precio, estado) VALUES
('Corte clásico', 45, 4500.00, 'ACTIVO'),
('Barba', 30, 3000.00, 'ACTIVO'),
('Corte + Barba', 60, 6500.00, 'ACTIVO'),
('Degradado', 60, 5500.00, 'ACTIVO'),
('Perfilado de cejas', 20, 2500.00, 'ACTIVO');

INSERT INTO barbero (nombre, apellido, especialidad, telefono, estado, id_sucursal) VALUES
('Juan', 'Pérez', 'Corte clásico', '3704-100001', 'ACTIVO', 1),
('Carlos', 'Ramírez', 'Corte + Barba', '3704-100002', 'ACTIVO', 1),
('Luis', 'Gómez', 'Degradado', '3704-100003', 'ACTIVO', 2),
('Pedro', 'Sánchez', 'Corte + Barba', '3704-100004', 'ACTIVO', 3),
('Martín', 'López', 'Corte clásico', '3704-100005', 'INACTIVO', 1),
('Emiliano', 'García', 'Degradado', '3704-100006', 'ACTIVO', 1);

INSERT INTO horario_barbero (id_barbero, dia_semana, hora_desde, hora_hasta, trabaja) VALUES
(1, 'LUNES', '09:00:00', '18:00:00', TRUE),
(1, 'MARTES', '09:00:00', '18:00:00', TRUE),
(1, 'MIERCOLES', '09:00:00', '18:00:00', TRUE),
(1, 'JUEVES', '09:00:00', '18:00:00', TRUE),
(1, 'VIERNES', '09:00:00', '18:00:00', TRUE),
(1, 'SABADO', '09:00:00', '13:00:00', TRUE),

(2, 'LUNES', '10:00:00', '19:00:00', TRUE),
(2, 'MARTES', '10:00:00', '19:00:00', TRUE),
(2, 'MIERCOLES', '10:00:00', '19:00:00', TRUE),
(2, 'JUEVES', '10:00:00', '19:00:00', TRUE),
(2, 'VIERNES', '10:00:00', '19:00:00', TRUE),
(2, 'SABADO', '10:00:00', '14:00:00', TRUE),

(3, 'LUNES', '09:00:00', '17:00:00', TRUE),
(3, 'MARTES', '09:00:00', '17:00:00', TRUE),
(3, 'MIERCOLES', '09:00:00', '17:00:00', TRUE),
(3, 'JUEVES', '09:00:00', '17:00:00', TRUE),
(3, 'VIERNES', '09:00:00', '17:00:00', TRUE);

INSERT INTO turno (
    id_cliente, id_barbero, id_servicio, id_sucursal,
    fecha, hora_inicio, hora_fin, estado, observaciones
) VALUES
(1, 1, 1, 1, '2025-05-23', '09:00:00', '09:45:00', 'COMPLETADO', 'Turno finalizado correctamente'),
(2, 1, 4, 1, '2025-05-23', '09:45:00', '10:45:00', 'COMPLETADO', 'Degradado bajo'),
(3, 2, 3, 1, '2025-05-23', '10:30:00', '11:30:00', 'EN_CURSO', 'Corte y barba'),
(4, 2, 2, 1, '2025-05-23', '11:30:00', '12:00:00', 'CONFIRMADO', 'Perfilado de barba'),
(5, 3, 4, 2, '2025-05-23', '11:00:00', '12:00:00', 'CONFIRMADO', 'Cliente nuevo'),
(6, 3, 1, 2, '2025-05-23', '12:00:00', '12:45:00', 'PROGRAMADO', 'Solicita corte tradicional');

-- ============================================================
-- 5. CONSULTA DE SUCURSALES ACTIVAS
-- ============================================================

SELECT 
    id_sucursal,
    nombre,
    direccion,
    telefono,
    estado
FROM sucursal
WHERE estado = 'ACTIVA';

-- ============================================================
-- 6. CONSULTA DE BARBEROS ACTIVOS POR SUCURSAL
-- ============================================================

SELECT 
    b.id_barbero,
    CONCAT(b.nombre, ' ', b.apellido) AS barbero,
    b.especialidad,
    s.nombre AS sucursal,
    b.telefono,
    b.estado
FROM barbero b
INNER JOIN sucursal s ON b.id_sucursal = s.id_sucursal
WHERE b.estado = 'ACTIVO'
ORDER BY s.nombre, b.apellido, b.nombre;

-- ============================================================
-- 7. CONSULTA DE AGENDA COMPLETA DE UN DÍA
-- ============================================================

SELECT
    t.id_turno,
    t.fecha,
    t.hora_inicio,
    t.hora_fin,
    CONCAT(c.nombre, ' ', c.apellido) AS cliente,
    sv.nombre AS servicio,
    CONCAT(b.nombre, ' ', b.apellido) AS barbero,
    s.nombre AS sucursal,
    t.estado,
    t.observaciones
FROM turno t
INNER JOIN cliente c ON t.id_cliente = c.id_cliente
INNER JOIN servicio sv ON t.id_servicio = sv.id_servicio
INNER JOIN barbero b ON t.id_barbero = b.id_barbero
INNER JOIN sucursal s ON t.id_sucursal = s.id_sucursal
WHERE t.fecha = '2025-05-23'
ORDER BY t.hora_inicio;

-- ============================================================
-- 8. CONSULTA DE AGENDA DE UNA SUCURSAL ESPECÍFICA
-- ============================================================

SELECT
    t.fecha,
    t.hora_inicio,
    t.hora_fin,
    CONCAT(c.nombre, ' ', c.apellido) AS cliente,
    sv.nombre AS servicio,
    CONCAT(b.nombre, ' ', b.apellido) AS barbero,
    t.estado
FROM turno t
INNER JOIN cliente c ON t.id_cliente = c.id_cliente
INNER JOIN servicio sv ON t.id_servicio = sv.id_servicio
INNER JOIN barbero b ON t.id_barbero = b.id_barbero
INNER JOIN sucursal s ON t.id_sucursal = s.id_sucursal
WHERE s.nombre = 'Centro'
  AND t.fecha = '2025-05-23'
ORDER BY t.hora_inicio;

-- ============================================================
-- 9. CONSULTA DE TURNOS DE UN BARBERO ESPECÍFICO
-- ============================================================

SELECT
    t.id_turno,
    t.fecha,
    t.hora_inicio,
    t.hora_fin,
    CONCAT(c.nombre, ' ', c.apellido) AS cliente,
    sv.nombre AS servicio,
    t.estado
FROM turno t
INNER JOIN cliente c ON t.id_cliente = c.id_cliente
INNER JOIN servicio sv ON t.id_servicio = sv.id_servicio
INNER JOIN barbero b ON t.id_barbero = b.id_barbero
WHERE b.nombre = 'Carlos'
  AND b.apellido = 'Ramírez'
  AND t.fecha = '2025-05-23'
ORDER BY t.hora_inicio;

-- ============================================================
-- 10. CONSULTA DE DISPONIBILIDAD OCUPADA DE UN BARBERO
-- ============================================================

SELECT
    CONCAT(b.nombre, ' ', b.apellido) AS barbero,
    t.fecha,
    t.hora_inicio,
    t.hora_fin,
    t.estado,
    CONCAT(c.nombre, ' ', c.apellido) AS cliente,
    sv.nombre AS servicio
FROM turno t
INNER JOIN barbero b ON t.id_barbero = b.id_barbero
INNER JOIN cliente c ON t.id_cliente = c.id_cliente
INNER JOIN servicio sv ON t.id_servicio = sv.id_servicio
WHERE t.id_barbero = 2
  AND t.fecha = '2025-05-23'
  AND t.estado <> 'CANCELADO'
ORDER BY t.hora_inicio;

-- ============================================================
-- 11. CONSULTA DE HORARIOS DE TRABAJO DE LOS BARBEROS
-- ============================================================

SELECT
    CONCAT(b.nombre, ' ', b.apellido) AS barbero,
    hb.dia_semana,
    hb.hora_desde,
    hb.hora_hasta,
    CASE 
        WHEN hb.trabaja = TRUE THEN 'Trabaja'
        ELSE 'No trabaja'
    END AS disponibilidad
FROM horario_barbero hb
INNER JOIN barbero b ON hb.id_barbero = b.id_barbero
ORDER BY b.apellido, b.nombre, hb.dia_semana;

-- ============================================================
-- 12. INSERTAR UN NUEVO CLIENTE
-- ============================================================

INSERT INTO cliente (nombre, apellido, telefono, email, observaciones)
VALUES (
    'Nicolás',
    'Castro',
    '3704-555007',
    'nicolascastro@mail.com',
    'Cliente agregado desde el sistema'
);

SELECT * FROM cliente;

-- ============================================================
-- 13. INSERTAR UN NUEVO TURNO
-- ============================================================

INSERT INTO turno (
    id_cliente,
    id_barbero,
    id_servicio,
    id_sucursal,
    fecha,
    hora_inicio,
    hora_fin,
    estado,
    observaciones
)
VALUES (
    7,
    2,
    3,
    1,
    '2025-05-23',
    '13:00:00',
    '14:00:00',
    'PROGRAMADO',
    'Turno registrado desde el prototipo'
);

SELECT
    t.id_turno,
    CONCAT(c.nombre, ' ', c.apellido) AS cliente,
    CONCAT(b.nombre, ' ', b.apellido) AS barbero,
    sv.nombre AS servicio,
    s.nombre AS sucursal,
    t.fecha,
    t.hora_inicio,
    t.hora_fin,
    t.estado
FROM turno t
INNER JOIN cliente c ON t.id_cliente = c.id_cliente
INNER JOIN barbero b ON t.id_barbero = b.id_barbero
INNER JOIN servicio sv ON t.id_servicio = sv.id_servicio
INNER JOIN sucursal s ON t.id_sucursal = s.id_sucursal
ORDER BY t.id_turno DESC;

-- ============================================================
-- 14. VALIDAR SI EXISTE SUPERPOSICIÓN DE HORARIOS
-- ============================================================

SELECT
    t.id_turno,
    CONCAT(b.nombre, ' ', b.apellido) AS barbero,
    t.fecha,
    t.hora_inicio,
    t.hora_fin,
    t.estado
FROM turno t
INNER JOIN barbero b ON t.id_barbero = b.id_barbero
WHERE t.id_barbero = 2
  AND t.fecha = '2025-05-23'
  AND t.estado <> 'CANCELADO'
  AND (
        '10:45:00' < t.hora_fin
        AND '11:45:00' > t.hora_inicio
      );

-- ============================================================
-- 15. REPROGRAMAR UN TURNO
-- ============================================================

UPDATE turno
SET 
    fecha = '2025-05-24',
    hora_inicio = '15:00:00',
    hora_fin = '16:00:00',
    estado = 'PROGRAMADO',
    observaciones = 'Turno reprogramado a solicitud del cliente'
WHERE id_turno = 3;

SELECT
    id_turno,
    fecha,
    hora_inicio,
    hora_fin,
    estado,
    observaciones
FROM turno
WHERE id_turno = 3;

-- ============================================================
-- 16. CANCELAR UN TURNO
-- ============================================================

UPDATE turno
SET 
    estado = 'CANCELADO',
    observaciones = 'Turno cancelado por el cliente'
WHERE id_turno = 4;

SELECT
    id_turno,
    fecha,
    hora_inicio,
    hora_fin,
    estado,
    observaciones
FROM turno
WHERE id_turno = 4;

-- ============================================================
-- 17. BORRADO FÍSICO DE UN REGISTRO DE PRUEBA
-- ============================================================
-- Nota: en un sistema real se recomienda cancelación lógica.
-- El borrado físico se incluye porque la consigna solicita borrado de registros.

DELETE FROM turno
WHERE id_turno = 7;

SELECT * FROM turno;

-- ============================================================
-- 18. REPORTE DE CANTIDAD DE TURNOS POR SUCURSAL
-- ============================================================

SELECT
    s.nombre AS sucursal,
    COUNT(t.id_turno) AS cantidad_turnos
FROM sucursal s
LEFT JOIN turno t ON s.id_sucursal = t.id_sucursal
GROUP BY s.id_sucursal, s.nombre
ORDER BY cantidad_turnos DESC;

-- ============================================================
-- 19. REPORTE DE TURNOS POR BARBERO
-- ============================================================

SELECT
    CONCAT(b.nombre, ' ', b.apellido) AS barbero,
    s.nombre AS sucursal,
    COUNT(t.id_turno) AS cantidad_turnos
FROM barbero b
INNER JOIN sucursal s ON b.id_sucursal = s.id_sucursal
LEFT JOIN turno t ON b.id_barbero = t.id_barbero
GROUP BY b.id_barbero, b.nombre, b.apellido, s.nombre
ORDER BY cantidad_turnos DESC;

-- ============================================================
-- 20. REPORTE DE SERVICIOS MÁS SOLICITADOS
-- ============================================================

SELECT
    sv.nombre AS servicio,
    COUNT(t.id_turno) AS cantidad_solicitada,
    SUM(sv.precio) AS ingreso_estimado
FROM servicio sv
LEFT JOIN turno t ON sv.id_servicio = t.id_servicio
GROUP BY sv.id_servicio, sv.nombre
ORDER BY cantidad_solicitada DESC;

-- ============================================================
-- 21. REPORTE DE INGRESOS ESTIMADOS POR DÍA
-- ============================================================

SELECT
    t.fecha,
    COUNT(t.id_turno) AS cantidad_turnos,
    SUM(sv.precio) AS ingresos_estimados
FROM turno t
INNER JOIN servicio sv ON t.id_servicio = sv.id_servicio
WHERE t.estado <> 'CANCELADO'
GROUP BY t.fecha
ORDER BY t.fecha;

-- ============================================================
-- 22. CONSULTAR TURNOS PENDIENTES O PROGRAMADOS
-- ============================================================

SELECT
    t.id_turno,
    t.fecha,
    t.hora_inicio,
    CONCAT(c.nombre, ' ', c.apellido) AS cliente,
    CONCAT(b.nombre, ' ', b.apellido) AS barbero,
    sv.nombre AS servicio,
    t.estado
FROM turno t
INNER JOIN cliente c ON t.id_cliente = c.id_cliente
INNER JOIN barbero b ON t.id_barbero = b.id_barbero
INNER JOIN servicio sv ON t.id_servicio = sv.id_servicio
WHERE t.estado IN ('PROGRAMADO', 'CONFIRMADO')
ORDER BY t.fecha, t.hora_inicio;

-- ============================================================
-- 23. CONSULTA PARA MOSTRAR DATOS DEL DASHBOARD
-- ============================================================

SELECT
    (SELECT COUNT(*) 
     FROM turno 
     WHERE fecha = '2025-05-23') AS turnos_del_dia,

    (SELECT COUNT(*) 
     FROM cliente) AS clientes_registrados,

    (SELECT COUNT(*) 
     FROM barbero 
     WHERE estado = 'ACTIVO') AS barberos_activos,

    (SELECT COUNT(*) 
     FROM sucursal 
     WHERE estado = 'ACTIVA') AS sucursales_activas;

-- ============================================================
-- 24. CONSULTA DE SERVICIOS ACTIVOS
-- ============================================================

SELECT
    id_servicio,
    nombre,
    duracion_min,
    precio,
    estado
FROM servicio
WHERE estado = 'ACTIVO'
ORDER BY nombre;

-- ============================================================
-- 25. CONSULTA DE CLIENTES CON HISTORIAL DE TURNOS
-- ============================================================

SELECT
    CONCAT(c.nombre, ' ', c.apellido) AS cliente,
    c.telefono,
    COUNT(t.id_turno) AS cantidad_turnos,
    MAX(t.fecha) AS ultimo_turno
FROM cliente c
LEFT JOIN turno t ON c.id_cliente = t.id_cliente
GROUP BY c.id_cliente, c.nombre, c.apellido, c.telefono
ORDER BY cantidad_turnos DESC;

-- ============================================================
-- 26. CONSULTA FINAL RECOMENDADA PARA CAPTURA COMPLETA
-- ============================================================

SELECT
    t.id_turno AS turno,
    s.nombre AS sucursal,
    CONCAT(c.nombre, ' ', c.apellido) AS cliente,
    CONCAT(b.nombre, ' ', b.apellido) AS barbero,
    sv.nombre AS servicio,
    sv.duracion_min AS duracion,
    sv.precio,
    t.fecha,
    t.hora_inicio,
    t.hora_fin,
    t.estado
FROM turno t
INNER JOIN sucursal s ON t.id_sucursal = s.id_sucursal
INNER JOIN cliente c ON t.id_cliente = c.id_cliente
INNER JOIN barbero b ON t.id_barbero = b.id_barbero
INNER JOIN servicio sv ON t.id_servicio = sv.id_servicio
ORDER BY t.fecha, t.hora_inicio;

-- ============================================================
-- FIN DEL SCRIPT
-- ============================================================
