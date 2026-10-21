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

Las comprobaciones conjuntas utilizaron datos ficticios y cubren ese recorrido; no representan una prueba completa de todas las funciones. Posteriormente, el trabajo `CompilarClienteJava21` también terminó en `SUCCESS` con JDK 21.0.12.1 y Maven 3.9.11.

## Plugins Maven solicitados para la práctica

Además del cambio de Java, el POM declara explícitamente estos plugins:

| Plugin | Versión | Cambio |
|---|---|---|
| sonar-maven-plugin | 5.5.0.6356 | Agregado con la versión solicitada |
| jacoco-maven-plugin | 0.8.15 | Agregado para instrumentar las pruebas y generar cobertura en verify |
| maven-surefire-plugin | 3.5.4 | Declarado explícitamente; antes se utilizaba la versión 3.1.2 heredada |

`mvn clean verify` ejecuta las pruebas con Surefire y genera `target/site/jacoco/index.html` y `target/site/jacoco/jacoco.xml`. El Jenkinsfile incluye la publicación de este XML mediante Coverage.

El plugin de Sonar queda disponible en Maven. Su incorporación no ejecuta automáticamente un análisis: todavía debe configurarse el servidor SonarQube, su URL y el token, y ejecutar `mvn sonar:sonar`. El token se debe proporcionar mediante una credencial de Jenkins o una variable de entorno, fuera del POM.

El POM original de Java 17 se mantiene como evidencia del estado inicial, sin agregarle estos plugins.

La comprobación de estos plugins en una copia de validación dentro del contenedor Jenkins, con JDK 21.0.12.1 y Maven 3.9.11, terminó en `BUILD SUCCESS`: una prueba aprobada y cobertura de 2/274 líneas (0.73 %). Maven resolvió Sonar 5.5.0.6356 mediante `help:describe`. La cobertura refleja únicamente la prueba existente de arranque. Esta validación es independiente del trabajo Jenkins; vuelve a construirlo para registrar el commit actualizado y publicar su cobertura.
