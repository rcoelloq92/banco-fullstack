-- ============================================================================
-- BaseDatos.sql - Prueba tecnica bancaria (PostgreSQL)
-- Esquema coherente con las entidades JPA del backend (com.banco.model):
--   Persona (herencia JOINED) <- Cliente (@PrimaryKeyJoinColumn persona_id)
--   Cuenta (FK cliente_id -> cliente.persona_id)
--   Movimiento (FK cuenta_id -> cuenta.id)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Tabla persona: datos personales base (raiz de la herencia JOINED)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS persona (
    id             BIGSERIAL PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL,
    genero         VARCHAR(20),
    edad           INTEGER,
    identificacion VARCHAR(20)  NOT NULL UNIQUE,
    direccion      VARCHAR(150),
    telefono       VARCHAR(15)
);

-- ----------------------------------------------------------------------------
-- Tabla cliente: hereda de persona compartiendo la PK (persona_id)
-- cliente_id es el identificador de negocio, unico e independiente de la PK
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cliente (
    persona_id BIGINT PRIMARY KEY REFERENCES persona (id),
    cliente_id VARCHAR(20)  NOT NULL UNIQUE,
    contrasena VARCHAR(100) NOT NULL,
    estado     BOOLEAN      NOT NULL
);

-- ----------------------------------------------------------------------------
-- Tabla cuenta: cuentas bancarias asociadas a un cliente
-- tipo_cuenta se persiste como texto del enum (AHORRO | CORRIENTE)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cuenta (
    id            BIGSERIAL PRIMARY KEY,
    numero_cuenta VARCHAR(20)    NOT NULL UNIQUE,
    tipo_cuenta   VARCHAR(20)    NOT NULL,
    saldo_inicial NUMERIC(15, 2) NOT NULL,
    estado        BOOLEAN        NOT NULL,
    cliente_id    BIGINT         NOT NULL REFERENCES cliente (persona_id)
);

-- ----------------------------------------------------------------------------
-- Tabla movimiento: transacciones sobre una cuenta
-- tipo_movimiento se persiste como texto del enum (CREDITO | DEBITO)
-- valor con signo (credito positivo, debito negativo); saldo = disponible
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS movimiento (
    id              BIGSERIAL PRIMARY KEY,
    fecha           TIMESTAMP      NOT NULL,
    tipo_movimiento VARCHAR(20)    NOT NULL,
    valor           NUMERIC(15, 2) NOT NULL,
    saldo           NUMERIC(15, 2) NOT NULL,
    cuenta_id       BIGINT         NOT NULL REFERENCES cuenta (id)
);

-- ============================================================================
-- Datos de ejemplo (casos de uso del enunciado)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Personas (se insertan primero por la herencia JOINED)
-- ----------------------------------------------------------------------------
INSERT INTO persona (nombre, genero, edad, identificacion, direccion, telefono) VALUES
    ('Jose Lema',           'Masculino', 35, '0912345678', 'Otavalo sn y principal',  '098254785'),
    ('Marianela Montalvo',  'Femenino',  32, '0923456789', 'Amazonas y NNUU',         '097548965'),
    ('Juan Osorio',         'Masculino', 28, '0934567890', '13 junio y Equinoccial',  '098874587');

-- ----------------------------------------------------------------------------
-- Clientes (PK compartida con persona via persona_id)
-- ----------------------------------------------------------------------------
INSERT INTO cliente (persona_id, cliente_id, contrasena, estado) VALUES
    ((SELECT id FROM persona WHERE identificacion = '0912345678'), 'jlema',      '1234', TRUE),
    ((SELECT id FROM persona WHERE identificacion = '0923456789'), 'mmontalvo',  '5678', TRUE),
    ((SELECT id FROM persona WHERE identificacion = '0934567890'), 'josorio',    '1245', TRUE);

-- ----------------------------------------------------------------------------
-- Cuentas (tipo_cuenta segun el enum: AHORRO | CORRIENTE)
-- ----------------------------------------------------------------------------
INSERT INTO cuenta (numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id) VALUES
    ('478758', 'AHORRO',    2000.00, TRUE, (SELECT persona_id FROM cliente WHERE cliente_id = 'jlema')),
    ('225487', 'CORRIENTE',  100.00, TRUE, (SELECT persona_id FROM cliente WHERE cliente_id = 'mmontalvo')),
    ('495878', 'AHORRO',       0.00, TRUE, (SELECT persona_id FROM cliente WHERE cliente_id = 'josorio')),
    ('496825', 'AHORRO',     540.00, TRUE, (SELECT persona_id FROM cliente WHERE cliente_id = 'mmontalvo')),
    ('585545', 'CORRIENTE', 1000.00, TRUE, (SELECT persona_id FROM cliente WHERE cliente_id = 'jlema'));

-- NOTA: los movimientos no se insertan aqui; se generan via API para que las
-- reglas de negocio calculen los saldos a partir del saldo inicial.
