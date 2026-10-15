-- Base de datos legacy (MySQL): recintos, eventos y asientos
SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS stagepass_legacy;
USE stagepass_legacy;

CREATE TABLE recinto (
    id      VARCHAR(20) PRIMARY KEY,
    nombre  VARCHAR(100) NOT NULL,
    ciudad  VARCHAR(60) NOT NULL,
    aforo   INT NOT NULL
);

CREATE TABLE evento (
    id          VARCHAR(20) PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    tipo        VARCHAR(20) NOT NULL,   -- CONCIERTO, TEATRO, FESTIVAL
    recinto_id  VARCHAR(20) NOT NULL,
    fecha_hora  DATETIME NOT NULL,
    estado      VARCHAR(20) NOT NULL,   -- PROGRAMADO, AGOTADO, CANCELADO
    FOREIGN KEY (recinto_id) REFERENCES recinto(id)
);

CREATE TABLE asiento (
    id           VARCHAR(20) PRIMARY KEY,
    recinto_id   VARCHAR(20) NOT NULL,
    codigo       VARCHAR(20) NOT NULL,  -- VIP-A1, General-01...
    precio_base  DECIMAL(12,2) NOT NULL,
    estado       VARCHAR(20) NOT NULL,  -- DISPONIBLE, BLOQUEADO, VENDIDO
    FOREIGN KEY (recinto_id) REFERENCES recinto(id)
);

INSERT INTO recinto VALUES ('R1', 'Coliseo Central', 'Bucaramanga', 12);
INSERT INTO recinto VALUES ('R2', 'Teatro Municipal', 'Bucaramanga', 6);

INSERT INTO evento VALUES ('E1', 'Noche de Rock', 'CONCIERTO', 'R1', '2026-12-05 20:00:00', 'PROGRAMADO');
INSERT INTO evento VALUES ('E2', 'Festival de Verano', 'FESTIVAL', 'R1', '2027-01-16 14:00:00', 'PROGRAMADO');
INSERT INTO evento VALUES ('E3', 'Obra de teatro', 'TEATRO', 'R2', '2026-11-20 19:30:00', 'PROGRAMADO');
INSERT INTO evento VALUES ('E4', 'Concierto cancelado', 'CONCIERTO', 'R1', '2026-12-12 21:00:00', 'CANCELADO');

INSERT INTO asiento VALUES ('A1', 'R1', 'VIP-A1', 250000, 'DISPONIBLE');
INSERT INTO asiento VALUES ('A2', 'R1', 'VIP-A2', 250000, 'DISPONIBLE');
INSERT INTO asiento VALUES ('A3', 'R1', 'VIP-A3', 250000, 'DISPONIBLE');
INSERT INTO asiento VALUES ('A4', 'R1', 'VIP-A4', 250000, 'DISPONIBLE');
INSERT INTO asiento VALUES ('A5', 'R1', 'General-01', 90000, 'DISPONIBLE');
INSERT INTO asiento VALUES ('A6', 'R1', 'General-02', 90000, 'DISPONIBLE');
INSERT INTO asiento VALUES ('A7', 'R1', 'General-03', 90000, 'DISPONIBLE');
INSERT INTO asiento VALUES ('A8', 'R1', 'General-04', 90000, 'DISPONIBLE');
INSERT INTO asiento VALUES ('A9', 'R1', 'General-05', 90000, 'DISPONIBLE');
INSERT INTO asiento VALUES ('A10', 'R1', 'General-06', 90000, 'DISPONIBLE');
INSERT INTO asiento VALUES ('A11', 'R1', 'General-07', 90000, 'DISPONIBLE');
INSERT INTO asiento VALUES ('A12', 'R1', 'General-08', 90000, 'VENDIDO');
INSERT INTO asiento VALUES ('B1', 'R2', 'Platea-A1', 120000, 'DISPONIBLE');
INSERT INTO asiento VALUES ('B2', 'R2', 'Platea-A2', 120000, 'DISPONIBLE');
INSERT INTO asiento VALUES ('B3', 'R2', 'Platea-A3', 120000, 'DISPONIBLE');
INSERT INTO asiento VALUES ('B4', 'R2', 'Platea-A4', 120000, 'DISPONIBLE');
INSERT INTO asiento VALUES ('B5', 'R2', 'Platea-A5', 120000, 'DISPONIBLE');
INSERT INTO asiento VALUES ('B6', 'R2', 'Platea-A6', 120000, 'DISPONIBLE');
