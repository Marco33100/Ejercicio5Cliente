# Ejercicio5Cliente

Cliente web del gestor de proyectos, desarrollado con Java 21, Spring Boot 3.2.5 y Thymeleaf. Consume la API REST de [Ejercicio5MyAPIGestor](https://github.com/Marco33100/Ejercicio5MyAPIGestor), disponible en `http://localhost:8081`.

## Requisitos

- JDK 21 y `JAVA_HOME` configurado con ese JDK.
- La API y su base MySQL en ejecución para registrar usuarios, iniciar sesión y gestionar proyectos.
- Conexión a Internet en el primer uso para descargar Maven y las dependencias.

El proyecto incluye Maven Wrapper 3.9.11; no es necesario instalar Maven globalmente.

## Compilar y ejecutar en Windows

Desde la carpeta de este repositorio, en PowerShell:

```powershell
java -version
.\mvnw.cmd --version
.\mvnw.cmd clean verify
java -jar target/Ejercicio5Cliente-0.0.1-SNAPSHOT.jar --server.port=8082
```

Abre [http://localhost:8082](http://localhost:8082). Se usa 8082 para poder ejecutar Jenkins en 8080. La API debe permanecer en 8081, que es la dirección utilizada por el cliente.

En Linux o macOS utiliza `./mvnw` en lugar de `.\mvnw.cmd`.

## Actualización a Java 21

El POM pasó de Java 17 a Java 21. Spring Boot y Maven Wrapper conservaron sus versiones. La compilación con JDK 21 y Maven 3.9.11 terminó en `BUILD SUCCESS`, con una prueba existente aprobada y sin errores.

También se comprobaron el arranque del JAR, las páginas iniciales y el registro y acceso de un usuario mediante la API y una base MySQL temporal.

- [POM original para Java 17](docs/pom-original.xml)
- [POM actualizado para Java 21](pom.xml)
- [Explicación de la actualización](docs/actualizacion-java21.md)

El historial conserva la importación del proyecto y de sus recursos, seguida de la actualización del POM a Java 21.

## Jenkins

Las herramientas configuradas son `JDK21` y `Maven3` (Maven 3.9.11). Su ejecución de verificación confirmó Java 21. La conexión de este repositorio con un trabajo de Jenkins y la configuración de análisis se realizan en la siguiente etapa; las pruebas indicadas arriba se ejecutaron localmente.
