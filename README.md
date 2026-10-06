# Banco Full Stack

API REST bancaria (clientes, cuentas, movimientos y reportes de estado de cuenta) construida con Spring Boot, acompañada de un frontend Angular para la gestión y consulta. El proyecto se levanta completo con Docker Compose o de forma local con Maven y npm.

## Tecnologías

- Java 17
- Spring Boot 3.3.4 (Web, Data JPA, Validation)
- PostgreSQL 15
- Angular 17
- Jest (pruebas de frontend)
- OpenPDF (generación del estado de cuenta en PDF)
- Docker y Docker Compose

## Arquitectura y decisiones de diseño

El backend sigue una arquitectura en capas con responsabilidades separadas:

- **Controller** (`com.banco.controller`): expone los endpoints REST, recibe y devuelve DTOs. No contiene lógica de negocio.
- **Service** (`com.banco.service` / `service.impl`): orquesta las reglas de negocio y las transacciones. Es la única capa que decide si una operación procede o no.
- **Repository** (`com.banco.repository`): acceso a datos con Spring Data JPA. Aísla la persistencia del resto de la aplicación (patrón Repository).
- **Model** (`com.banco.model`): entidades JPA con validación a nivel de modelo mediante Bean Validation, de modo que las restricciones viven junto al dato y no repartidas por los controladores.
- **DTO y Mapper** (`com.banco.dto`, `com.banco.mapper`): las entidades nunca salen por la API; se traducen a DTOs. Esto evita exponer el modelo de persistencia, previene problemas de serialización por lazy loading y permite evolucionar la base de datos sin romper el contrato REST.

Decisiones puntuales:

- **Herencia JOINED para Persona/Cliente**: `Persona` es la base y `Cliente` la extiende con `@Inheritance(strategy = JOINED)`. Cada clase conserva su propia tabla y comparten la PK. Se eligió JOINED frente a SINGLE_TABLE porque mantiene el esquema normalizado (sin columnas nulas de otras subclases) y frente a TABLE_PER_CLASS porque permite agregar otros subtipos de persona a futuro sin duplicar columnas ni complicar las consultas polimórficas.
- **Patrón Strategy para los movimientos** (`com.banco.service.strategy`): cada tipo de movimiento implementa la interfaz `OperacionMovimiento` (`CreditoOperacion`, `DebitoOperacion`) y recibe un `ContextoOperacion` con el saldo, el valor solicitado y los límites. Agregar un nuevo tipo de operación (por ejemplo una transferencia) es crear una clase nueva, sin tocar `MovimientoServiceImpl`.
- **Manejo centralizado de errores**: `ManejadorGlobalExcepciones` (`@RestControllerAdvice`) traduce las excepciones de negocio (`SaldoNoDisponibleException`, `CupoDiarioExcedidoException`, `RecursoNoEncontradoException`, `RegistroDuplicadoException`) y los errores de validación a respuestas HTTP consistentes con un cuerpo de error único (`RespuestaError`). Los controladores no manejan errores.

## Reglas de negocio implementadas

1. Los créditos se registran con valor positivo y los débitos con valor negativo; el saldo de la cuenta se actualiza con cada movimiento.
2. Si un débito supera el saldo disponible, la operación se rechaza con el mensaje `Saldo no disponible`.
3. Existe un cupo diario de retiro de 1000, parametrizable vía `aplicacion.movimientos.limite-diario-retiro` en `application.yml`. Al excederlo, la operación se rechaza con el mensaje `Cupo diario Excedido`.
4. Reporte de estado de cuenta por cliente y rango de fechas: devuelve las cuentas con sus saldos y el detalle de movimientos en JSON, y opcionalmente el mismo reporte como PDF codificado en base64 (generado con OpenPDF).

## Endpoints

### Clientes `/api/clientes`

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/clientes` | Lista todos los clientes |
| GET | `/api/clientes/{id}` | Obtiene un cliente por id |
| POST | `/api/clientes` | Crea un cliente |
| PUT | `/api/clientes/{id}` | Actualiza un cliente completo |
| PATCH | `/api/clientes/{id}` | Actualiza parcialmente un cliente |
| DELETE | `/api/clientes/{id}` | Elimina un cliente |

### Cuentas `/api/cuentas`

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/cuentas` | Lista todas las cuentas |
| GET | `/api/cuentas/{id}` | Obtiene una cuenta por id |
| POST | `/api/cuentas` | Crea una cuenta asociada a un cliente |
| PUT | `/api/cuentas/{id}` | Actualiza una cuenta completa |
| PATCH | `/api/cuentas/{id}` | Actualiza parcialmente una cuenta |
| DELETE | `/api/cuentas/{id}` | Elimina una cuenta |

### Movimientos `/api/movimientos`

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/movimientos` | Lista todos los movimientos |
| GET | `/api/movimientos/{id}` | Obtiene un movimiento por id |
| POST | `/api/movimientos` | Registra un movimiento (aplica las reglas de negocio) |
| DELETE | `/api/movimientos/{id}` | Elimina un movimiento y revierte su efecto en el saldo |

No se exponen PUT ni PATCH sobre movimientos a propósito: una transacción registrada es inmutable. Editar un movimiento ya aplicado dejaría el saldo en un estado inconsistente respecto al historial; la corrección se hace eliminando el movimiento (que revierte el saldo) y registrando uno nuevo.

### Reportes `/api/reportes`

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/reportes?clienteId={id}&fechaInicio={yyyy-MM-dd}&fechaFin={yyyy-MM-dd}` | Estado de cuenta en JSON |
| GET | `/api/reportes?clienteId={id}&fechaInicio={yyyy-MM-dd}&fechaFin={yyyy-MM-dd}&formato=pdf` | Estado de cuenta en JSON incluyendo el PDF en base64 |

## Cómo ejecutar

### Con Docker (recomendado)

```bash
docker compose up --build
```

- Frontend: http://localhost
- API: http://localhost:8080

El compose levanta PostgreSQL, el backend y el frontend (nginx con proxy `/api/` hacia el backend). `BaseDatos.sql` se monta en `/docker-entrypoint-initdb.d/`, por lo que el esquema se crea automáticamente la primera vez que se inicializa el contenedor de la base de datos; no hay que ejecutarlo a mano.

### En local

Requisitos: Java 17, Node 18+, PostgreSQL 15 corriendo en `localhost:5432` con base `banco` y usuario/clave `banco`/`banco123` (o sobreescribir `DB_URL`, `DB_USER`, `DB_PASSWORD` por variables de entorno). Ejecutar `BaseDatos.sql` sobre la base antes de arrancar.

Backend:

```bash
cd backend
./mvnw spring-boot:run
```

Frontend:

```bash
cd frontend
npm install
npm start
```

## Pruebas

Backend:

```bash
cd backend
./mvnw test
```

Frontend (Jest):

```bash
cd frontend
npm test
```

## Colección Postman

En `postman/Banco.postman_collection.json` está la colección con los requests de clientes, cuentas, movimientos y reportes, lista para importar en Postman.
