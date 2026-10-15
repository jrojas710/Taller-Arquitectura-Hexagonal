# StagePass Live - Taller Arquitectura Hexagonal

**Integrantes:**
- Juan Rojas
- Juan David Bernal
- Andres Felipe Jaimes Mantilla

**Materia:** Arquitectura de Software - UNAB

## Contenido

- `docs/diagrama-clases.drawio` - diagrama de clases UML (se abre en draw.io). Tambien esta en `.png` y `.pdf`.
- `src/` - codigo fuente (Java 21 + Spring Boot).
- `db/legacy-mysql.sql` - script de la base legacy MySQL con datos de prueba.
- `docker-compose.yml` - levanta MySQL y MongoDB.

## Estructura (hexagonal)

```
domain/            -> nucleo: Evento, Asiento, Cliente, Orden, Boleto, CalculadoraTarifas, excepciones
application/
  port/in/         -> puertos primarios: CompraUseCase, PagoUseCase, BoletoUseCase
  port/out/        -> puertos secundarios: RecintoRepository, OrdenRepository, BoletoRepository,
                      PasarelaPagoPort, NotificacionPort, FacturacionPort, GeneradorBoletoPort
  service/         -> casos de uso: CompraService, PagoService, BoletoService
infrastructure/
  adapter/in/      -> CompraController, WebhookController, LiberarReservasJob, ManejadorErrores
  adapter/out/     -> MySQL, MongoDB, Wompi, PayPal, PSE, WhatsApp, SendGrid, DIAN, generador de boletos
  config/          -> BeanConfig (crea los servicios e inyecta los adaptadores por constructor)
```

El paquete `domain` y el paquete `application` no importan nada de Spring ni de bases de datos. Los servicios solo conocen las interfaces de `port/out`, y los adaptadores de `infrastructure` las implementan (inversion de dependencias). Las clases del nucleo no tienen anotaciones; se crean en `BeanConfig`.

## Reglas de negocio

- **Bloqueo de 10 minutos:** al crear la orden los asientos quedan bloqueados y la orden expira a los 10 min. Un asiento de una orden vencida ya se considera libre. Ademas `LiberarReservasJob` corre cada minuto y pasa las ordenes vencidas a EXPIRADA.
- **Tarifas:** 1-2 boletos tarifa 10% sin descuento, 3-5 boletos tarifa 8% y descuento 5%, mas de 5 tarifa 5% y descuento 10% (`CalculadoraTarifas`). Total = subtotal + tarifa - descuento.
- **Limite:** maximo 6 boletas por cliente en el mismo evento.
- **Emision:** los boletos solo se generan si la pasarela confirma el pago (`consultarEstado`), no basta con el webhook. Si el pago llega despues de los 10 minutos la orden queda EXPIRADA.
- **Firma:** SHA-256 de los datos de la orden (orden, evento, cliente, asiento, codigo) + la clave secreta del evento. La validacion recalcula el hash y lo compara.

## Adaptadores

Implementados completos: **MySQL** (consulta de eventos y asientos legacy con JdbcTemplate) y **MongoDB** (ordenes y boletos con Spring Data). Tambien el generador de boletos (SHA-256 + QR).

Mocks (imprimen en consola): PayPal, Wompi, PSE, WhatsApp, SendGrid y DIAN.

## Como ejecutar

```
docker compose up -d
mvn spring-boot:run
```

Pruebas (no necesitan bases de datos, usan implementaciones en memoria de los puertos):

```
mvn test
```

## Endpoints

| Metodo | Ruta | |
|---|---|---|
| GET | /api/eventos/{id}/asientos | asientos con su estado |
| POST | /api/ordenes | iniciar compra |
| GET | /api/ordenes/{id} | orden y boletos |
| POST | /api/webhooks/wompi, /paypal, /pse | notificacion de la pasarela |
| POST | /api/boletos/validar | validar un boleto |

Ejemplo de compra:

```json
POST /api/ordenes
{
  "eventoId": "E1",
  "clienteId": "1098765432",
  "nombre": "Juan Rojas",
  "correo": "juan@correo.com",
  "telefono": "3001234567",
  "nacionalidad": "Colombiana",
  "asientoIds": ["A1", "A2", "A3"],
  "metodoPago": "WOMPI"
}
```

Despues se simula la confirmacion de Wompi con la `referenciaPago` que devuelve la orden:

```json
POST /api/webhooks/wompi
{ "event": "transaction.updated", "data": { "transaction": { "reference": "WOMPI-...", "status": "APPROVED" } } }
```
