# Evaluación 03 — Gestión de pedidos de restaurante

Sistema de laboratorio para registrar clientes, platos y pedidos. Su objetivo es practicar la separación de responsabilidades en tres microservicios Spring Boot, cada uno con su propia base MySQL. El pedido consulta los otros servicios por REST; solo guarda sus IDs como referencias y conserva el precio aplicado al momento de crear cada detalle.

## Arquitectura

| Proyecto | Responsabilidad | Puerto `dev` | Base de datos |
|---|---|---:|---|
| `cliente-service` | CRUD de clientes y correo único | 8081 | `bd_cliente_eval03` |
| `plato-service` | CRUD de platos, precios y disponibilidad | 8082 | `bd_plato_eval03` |
| `pedido-service` | Pedidos, detalles, total y estado | 8083 | `bd_pedido_eval03` |

Ver [DIAGRAMA.md](DIAGRAMA.md). `pedido-service` consulta `GET /api/clientes/{id}` y `GET /api/platos/{id}` mediante `RestClient`. Valida existencia y disponibilidad, obtiene el precio actual y calcula `subtotal = precio × cantidad` y `total = suma de subtotales` con `BigDecimal`. Cliente y plato no se relacionan mediante JPA con la base de pedidos.

## Tecnologías y requisitos

- Java 21, Maven 3.9 o el Maven Wrapper de cada proyecto, Spring Boot 4.1.2.
- Spring Web MVC, Spring Data JPA, Hibernate, Jakarta Validation, MySQL Connector/J y DevTools.
- MySQL local activo, por ejemplo mediante XAMPP; SQLyog puede usarse para comprobar las bases.
- IntelliJ IDEA y Postman para ejecución y pruebas manuales.
- Las tres bases indicadas en la tabla deben existir. No se requiere una contraseña si la cuenta local realmente no la tiene.

## Configuración y ejecución en IntelliJ

1. Abre la carpeta principal en IntelliJ y configura el SDK del proyecto como Java 21. Importa o recarga los tres proyectos Maven.
2. Inicia MySQL en XAMPP y comprueba en SQLyog las bases y las credenciales de conexión.
3. Si los valores locales predeterminados no coinciden con tu entorno, agrega las variables de entorno de la sección siguiente a cada configuración de ejecución.
4. Ejecuta `ClienteServiceApplication`, luego `PlatoServiceApplication` y finalmente `PedidoServiceApplication`. Las clases están en `src/main/java/com/eneque/...` de cada proyecto. Mantén las tres aplicaciones abiertas.
5. Comprueba en la consola que arrancaron en 8081, 8082 y 8083. La colección de Postman incluida permite probar las APIs.

Cada servicio tiene `application.yml` con perfil predeterminado `dev`, `application-dev.yml` y `application-prod.yml`. En `dev`, la URL MySQL apunta a `localhost:3306`, el usuario predeterminado es `root`, la contraseña predeterminada está vacía y Hibernate usa `ddl-auto: update`. Al iniciar `dev`, Hibernate puede crear o actualizar tablas de la base correspondiente. En `prod`, Hibernate usa `ddl-auto: validate`: exige un esquema ya preparado y no lo actualiza. Las URLs y credenciales de producción se proporcionan mediante variables de entorno; no hay contraseñas reales en los YAML. Para activar producción, configura `SPRING_PROFILES_ACTIVE=prod`.

| Servicio | Variables de conexión `dev` y `prod` | Puerto opcional en `prod` |
|---|---|---|
| Cliente | `CLIENTE_DB_URL`, `CLIENTE_DB_USERNAME`, `CLIENTE_DB_PASSWORD` | `CLIENTE_PORT` (8081) |
| Plato | `PLATO_DB_URL`, `PLATO_DB_USERNAME`, `PLATO_DB_PASSWORD` | `PLATO_PORT` (8082) |
| Pedido | `PEDIDO_DB_URL`, `PEDIDO_DB_USERNAME`, `PEDIDO_DB_PASSWORD` | `PEDIDO_PORT` (8083) |

`pedido-service` admite además `CLIENTE_SERVICE_URL` y `PLATO_SERVICE_URL`. En `dev` sus valores predeterminados son `http://localhost:8081` y `http://localhost:8082`; en `prod` son obligatorios. No incluyas contraseñas en capturas ni en archivos entregados.

## API REST

| Servicio | Listar | Buscar por ID | Crear | Actualizar | Eliminar |
|---|---|---|---|---|---|
| Cliente | `GET /api/clientes` | `GET /api/clientes/{id}` | `POST /api/clientes` | `PUT /api/clientes/{id}` | `DELETE /api/clientes/{id}` |
| Plato | `GET /api/platos` | `GET /api/platos/{id}` | `POST /api/platos` | `PUT /api/platos/{id}` | `DELETE /api/platos/{id}` |
| Pedido | `GET /api/pedidos` | `GET /api/pedidos/{id}` | `POST /api/pedidos` | `PUT /api/pedidos/{id}/estado` | `DELETE /api/pedidos/{id}` |

Las rutas anteriores son relativas a `http://localhost:8081`, `:8082` y `:8083`, respectivamente. Envía `Content-Type: application/json` en POST y PUT. `DELETE` devuelve 204 sin cuerpo. La creación devuelve 201; consultas y actualizaciones exitosas, 200.

### Ejemplos JSON

Cliente, para `POST /api/clientes` o `PUT /api/clientes/{id}`:

```json
{"nombre":"Ana","apellido":"Rojas","correo":"ana.rojas@example.com","telefono":"999888777"}
```

Plato, para `POST /api/platos` o `PUT /api/platos/{id}`:

```json
{"nombre":"Ceviche","descripcion":"Pescado fresco con limón","precio":29.90,"disponible":true}
```

Pedido con dos detalles, para `POST /api/pedidos` (requiere que existan el cliente 1 y los platos 1 y 2):

```json
{"idCliente":1,"detalles":[{"idPlato":1,"cantidad":2},{"idPlato":2,"cantidad":1}]}
```

El cliente HTTP no envía `precio`, `subtotal` ni `total`: el servidor los calcula a partir de `plato-service`. Para `PUT /api/pedidos/{id}/estado`:

```json
{"estado":"CONFIRMADO"}
```

Los estados son `PENDIENTE`, `CONFIRMADO` y `CANCELADO`. Desde `PENDIENTE` se puede confirmar o cancelar; desde `CONFIRMADO` se puede cancelar; `CANCELADO` es final. Repetir el estado actual está permitido.

## Validaciones y errores

- Cliente: nombre, apellido, correo y teléfono obligatorios; formato de correo válido; correo único. El servicio normaliza el correo a minúsculas y sin espacios exteriores.
- Plato: nombre, descripción y disponibilidad obligatorios; precio decimal mayor que cero, con hasta dos decimales.
- Pedido: `idCliente` e `idPlato` positivos, lista de detalles no vacía y cantidades positivas. Se rechazan clientes o platos inexistentes, platos no disponibles y transiciones de estado inválidas.
- `400`: JSON o datos inválidos. `404`: recurso solicitado o referencia externa inexistente. `409`: correo duplicado, plato no disponible o transición inválida. `502`: respuesta o fallo HTTP de servicio externo. `503`: servicio externo inaccesible o tiempo de espera. Los errores controlados devuelven `estado` y `mensaje` en JSON.

## Pruebas

Cada proyecto tiene pruebas unitarias y una prueba `contextLoads`. Desde la terminal, en cada carpeta de servicio, ejecuta `mvn test` (o `mvnw.cmd test` en Windows). La prueba de contexto requiere que MySQL esté disponible y puede aplicar `ddl-auto: update` en `dev`. Para ejecutar solo las pruebas unitarias sin acceder a MySQL:

```powershell
mvn -f cliente-service/pom.xml '-Dtest=ClienteServiceTest' test
mvn -f plato-service/pom.xml '-Dtest=PlatoServiceTest' test
mvn -f pedido-service/pom.xml '-Dtest=PedidoServiceTest,CrearPedidoRequestTest' test
```

Importa `Evaluacion03-Restaurante.postman_collection.json` en Postman. Revisa sus variables y ejecuta manualmente los casos deseados; la colección no ejecuta solicitudes por sí sola. Usa los casos de error y confirma el JSON de un pedido con dos detalles. Para la entrega, conserva evidencias de respuestas exitosas y fallidas, y evidencia SQL o capturas de las tres bases. No incluyas `target`, `.idea` ni archivos con contraseñas en el paquete final.
