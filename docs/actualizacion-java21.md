# Actualización del cliente a Java 21

Fecha: 20 de octubre de 2026.

En `pom.xml`, la propiedad `<java.version>` cambió de `17` a `21`. Se conservaron Spring Boot 3.2.5, Maven Wrapper 3.9.11, las dependencias y el código de la aplicación.

Spring Boot 3.2.5 admite Java 21 según sus [requisitos oficiales](https://docs.spring.io/spring-boot/docs/3.2.5/reference/html/getting-started.html).

## Evidencia antes y después

- Antes: [docs/pom-original.xml](pom-original.xml), con Java 17.
- Después: [pom.xml](../pom.xml), con Java 21.

El historial conserva el POM anterior durante la importación del proyecto y sus recursos. El commit de actualización aplica Java 21 y añade la documentación de ejecución.

## Verificación

- Entorno local: Eclipse Temurin JDK 21.0.11 y Maven 3.9.11, en Windows.
- `clean verify`: `BUILD SUCCESS`.
- Pruebas existentes: 1 aprobada, 0 fallos, 0 errores, 0 omitidas.
- Clases de la aplicación: bytecode 65, correspondiente a Java 21.
- JAR ejecutable generado y arrancado correctamente.
- `/`, `/login` y `/register`: HTTP 200 con contenido HTML.
- Comprobación conjunta con la API y MySQL temporal: registro, acceso válido, respuesta con contraseña incorrecta y consulta del usuario guardado.

Las comprobaciones conjuntas utilizaron datos ficticios y cubren ese recorrido; no representan una prueba completa de todas las funciones. La ejecución desde el trabajo de Jenkins se configura en la siguiente etapa.
