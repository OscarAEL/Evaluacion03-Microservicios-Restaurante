# Conclusiones individuales

1. Aprendí a separar clientes, platos y pedidos en tres proyectos Spring Boot. Cada uno tiene un puerto y una base de datos propios, lo que me ayudó a entender la responsabilidad de cada microservicio.
2. Aprendí a usar entidades y repositorios JPA para guardar datos en MySQL. En pedidos relacioné cada detalle con su pedido, mientras que los IDs de cliente y plato quedaron como referencias a otros servicios.
3. Aprendí a validar los datos de entrada con DTO y Jakarta Validation. Comprobé que una solicitud inválida puede recibir una respuesta HTTP clara, en vez de guardar datos incorrectos.
4. Aprendí a consultar otras APIs con `RestClient`. Al crear un pedido, el servicio verifica el cliente y los platos, toma el precio del plato y calcula el subtotal y el total con `BigDecimal`.
5. Aprendí a configurar perfiles `dev` y `prod` en YAML y a usar variables de entorno para las conexiones. También practiqué las pruebas de las rutas REST con Postman y la revisión de datos en SQLyog.

Una dificultad fue comprobar la compilación y las pruebas con Maven cuando faltaban artefactos en la caché local o el entorno restringía el acceso a ella. La solucioné usando Java 21, recargando las dependencias necesarias y ejecutando pruebas unitarias que no escriben en MySQL. Después verifiqué manualmente los servicios con IntelliJ, Postman y SQLyog.
