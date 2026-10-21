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

El repositorio incluye un [Jenkinsfile](Jenkinsfile) que descarga el proyecto, muestra las versiones de Java y Maven, ejecuta `mvn clean verify` y publica los resultados de las pruebas existentes.

Requiere estas herramientas en **Administrar Jenkins > Tools**:

- JDK con nombre `JDK21`, ruta `/opt/java/openjdk` en el contenedor Jenkins.
- Maven con nombre `Maven3`, versión 3.9.11.
- Complemento JUnit para publicar los resultados.

Para utilizarlo, crea un trabajo de tipo **Pipeline** llamado `CompilarClienteJava21` y configura:

- Definition: **Pipeline script from SCM**.
- SCM: **Git**.
- Repository URL: `https://github.com/Marco33100/Ejercicio5Cliente.git`.
- Credentials: ninguna, porque el repositorio es público.
- Branch Specifier: `*/main`.
- Script Path: `Jenkinsfile`.

Guarda y selecciona **Construir ahora**. En la consola deben aparecer Java 21, Maven 3.9.11, `BUILD SUCCESS` y `Finished: SUCCESS`.

Las pruebas y verificaciones descritas arriba se realizaron localmente. La ejecución del trabajo del cliente en Jenkins debe comprobarse con su propia consola. Los análisis de cobertura y SonarQube se configuran por separado.
