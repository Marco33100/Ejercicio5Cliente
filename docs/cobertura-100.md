# Cobertura Java del frontend: 100 %

Verificacion realizada el 27 de octubre de 2026 con JDK 21, Maven 3.9.11, Surefire 3.5.4 y JaCoCo 0.8.15, dentro del contenedor Jenkins.

| Metrica | Cubiertos | Sin cubrir | Cobertura |
|---|---:|---:|---:|
| Metodos | 97 | 0 | 100 % |
| Lineas | 259 | 0 | 100 % |
| Instrucciones | 849 | 0 | 100 % |
| Clases | 7 | 0 | 100 % |

La suite tiene **34 pruebas**, todas aprobadas, sin errores ni omisiones. No hay ramas condicionales evaluables en el codigo Java del cliente; JaCoCo no genera un contador de ramas para este proyecto. No se agregaron exclusiones de cobertura.

## Que se verifica

- `GestorControllerTest`: rutas y vistas, existencia de las plantillas publicas, atributos de los formularios, conversion de datos del formulario a JSON, consultas, identificadores enviados a la API, eliminaciones DELETE, login, listas vacias y errores HTTP del backend.
- Las llamadas usan un RestTemplate real conectado a MockRestServiceServer. Se verifican URL, metodo, contenido enviado y respuesta. No se necesita iniciar una API real para estas pruebas del cliente.
- `ContratoModelosJsonTest`: comprueba los nombres, tipos y valores de los cinco modelos al serializar y deserializar JSON, usando valores esperados independientes.
- `Ejercicio5ClienteMainTest`: invoca el `main` real, inicia Spring Boot sin ocupar un puerto HTTP, comprueba que el controlador este disponible y cierra el contexto.
- Se conserva la prueba de arranque existente.

## Cambio de produccion para probar el cliente HTTP

El controlador reutiliza una instancia RestTemplate. El constructor publico sin argumentos crea el cliente HTTP habitual; un constructor de paquete permite proporcionarlo en las pruebas. Se conservaron las rutas, las URLs de la API y las respuestas existentes. Se eliminaron las creaciones repetidas del mismo cliente en cada metodo.

## Reproducir

Ejecuta `mvn --batch-mode --no-transfer-progress clean verify -Dsurefire.useFile=false`, o `./mvnw clean verify` en Linux y `mvnw.cmd clean verify` en Windows. Los reportes quedan en `target/site/jacoco/index.html` y `target/site/jacoco/jacoco.xml`.

En Jenkins, vuelve a construir **CompilarClienteJava21**. Su pipeline ya descarga `main` y publica JaCoCo; este cambio no requiere reemplazar el script. La verificacion directa en el contenedor no es una nueva ejecucion de ese trabajo: su resultado se actualiza al reconstruirlo.

Esta cobertura corresponde al codigo Java instrumentado por JaCoCo. No representa cobertura de JavaScript ni una prueba visual o de extremo a extremo de todas las pantallas HTML.
