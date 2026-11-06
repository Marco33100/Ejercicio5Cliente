# Ejercicio5Cliente

Cliente web del gestor de proyectos con Java 21, Spring Boot 3.2.5 y Thymeleaf. Consume la API en `http://localhost:8081`.

## Requisitos

- JDK 21 y `JAVA_HOME` configurado con ese JDK.
- Maven Wrapper incluido, configurado con Maven 3.9.11.
- Conexión a Internet en el primer uso para resolver dependencias.


## Compilar y ejecutar en Windows

Desde la raíz del repositorio, en PowerShell:

```powershell
java -version
.\mvnw.cmd --version
.\mvnw.cmd clean verify
java -jar target/Ejercicio5Cliente-0.0.1-SNAPSHOT.jar --server.port=8082
```

Abre [http://localhost:8082](http://localhost:8082). La API y MySQL deben estar iniciados para usar las operaciones del gestor. Se utiliza 8082 para dejar 8080 a Jenkins.

En Linux o macOS utiliza `./mvnw` y configura las mismas variables de entorno si corresponde.

## Pruebas y cobertura

Resultado conservado de Jenkins **CompilarClienteJava21 #7**, verificado el **28 de octubre de 2026**: **SUCCESS**. 34 pruebas aprobadas; 97/97 métodos y 259/259 líneas cubiertos al 100 %. No hay ramas evaluables en este reporte. No hubo fallos, errores ni omisiones. Estas evidencias corresponden a esa revisión y no implican que se haya ejecutado una compilación nueva al editar este README.

Las pruebas comprueban rutas, vistas, formularios, contratos JSON, peticiones a la API y arranque real de Spring. Las llamadas HTTP se verifican con MockRestServiceServer y RestTemplate.

`clean verify` ejecuta Surefire y genera `target/site/jacoco/index.html` y `target/site/jacoco/jacoco.xml`. Coverage de Jenkins publica el XML. No se añadieron exclusiones manuales de clases o métodos. La cobertura mide ejecución del código Java; no acredita la calidad completa de las aserciones ni la cobertura visual de HTML o JavaScript.

Consulta el [detalle de cobertura](docs/cobertura-100.md).

## Jenkins

Configura las herramientas `JDK21` con ruta `/opt/java/openjdk` y `Maven3` con versión 3.9.11. Crea el trabajo **CompilarClienteJava21**, selecciona **Pipeline script** y pega el [Jenkinsfile](Jenkinsfile) completo. Activa **Use Groovy Sandbox**. El pipeline descarga `main`, muestra el POM y las versiones y ejecuta `clean verify` con `steps { dir('.') { sh ... } }`.

El trabajo publica JUnit y Coverage y ejecuta SonarQube con la clave `Ejercicio5Cliente`. Requiere JUnit, Coverage y SonarQube Scanner for Jenkins. Configura la instalación `SonarServer` y su token en Jenkins Credentials.

Un cambio del Jenkinsfile en GitHub no reemplaza automáticamente el texto de un trabajo configurado con Pipeline script. Para incorporar cambios del script, cópialo de nuevo a Jenkins.

## Herramientas Maven

El POM utiliza Java 21 y declara explícitamente Sonar Maven Plugin **5.5.0.6356**, JaCoCo **0.8.15** y Maven Surefire **3.5.4**. El análisis Sonar se ejecuta en una etapa independiente después de generar cobertura; declarar el plugin no envía un análisis por sí solo.

## Historial de la actualización

- [POM actual](pom.xml)
- [POM original](docs/pom-original.xml)
- [Cambios de Java 21 y plugins](docs/actualizacion-java21.md)
- [Repositorio de la API](https://github.com/Marco33100/Ejercicio5MyAPIGestor)
