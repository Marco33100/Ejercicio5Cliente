# Análisis del cliente con SonarQube

El Jenkinsfile añade una etapa `SonarQube Analysis` después de compilar,
ejecutar las pruebas y publicar JaCoCo. Utiliza el servidor Jenkins
`SonarServer`, con su credencial existente, y Sonar Maven Plugin 5.5.0.6356.

El análisis se publica con una clave propia, `Ejercicio5Cliente`, para que no
reemplace los resultados de `Ejercicio5MyAPIGestor`. Se envía la cobertura
Java de `target/site/jacoco/jacoco.xml` y se excluyen los directorios `target`
del análisis de fuentes. El XML de cobertura sigue leyéndose desde `target`.

## Configuración del trabajo

1. En Jenkins, abrir `CompilarClienteJava21` y entrar a **Configurar**.
2. Mantener **Definition: Pipeline script** y **Use Groovy Sandbox**.
3. Reemplazar el script completo por el contenido actualizado del Jenkinsfile.
4. Guardar y ejecutar **Construir ahora**.
5. Tras el procesamiento del análisis, abrir SonarQube, **Projects**, y buscar
   **Ejercicio5Cliente**.

Jenkins está en un contenedor: el servidor SonarQube se alcanza mediante
`http://host.docker.internal:9000`. Desde el navegador de Windows se abre
`http://localhost:9000`. Se reutiliza el token guardado en Jenkins, sin
incluirlo en el repositorio ni en el script.

El token debe tener permiso para ejecutar análisis y, si el proyecto todavía
no existe, permiso para crearlo. Si se muestra un error de autorización,
crear `Ejercicio5Cliente` en SonarQube con esa clave y conceder el permiso de
análisis al usuario del token.

El análisis de SonarQube y su Quality Gate son distintos del resultado de las
pruebas. Esta etapa envía el análisis; no espera el Quality Gate. El 100 % de
JaCoCo corresponde al código Java instrumentado, no a JavaScript o HTML.

Validación del cambio: sintaxis Groovy y de los bloques shell comprobada.
El envío efectivo y los resultados de SonarQube se verificarán al ejecutar
el trabajo con su credencial.

## Atención de la incidencia java:S6829

El primer análisis del cliente mostró una incidencia media de fiabilidad y
mantenibilidad: `GestorController` tiene dos constructores y ninguno indicaba
explícitamente cuál debe utilizar Spring. Se añadió `@Autowired` al constructor
público sin argumentos, conservando su comportamiento de crear el RestTemplate
y el constructor auxiliar que permite probar las peticiones HTTP.

Se verificaron nuevamente el arranque real de Spring y las 34 pruebas: todas
aprobadas, sin fallos, errores u omisiones. JaCoCo conserva 97/97 métodos y
259/259 líneas, 100 %, sin exclusiones. El cierre de la incidencia debe
comprobarse ejecutando nuevamente `CompilarClienteJava21` y esperando el
procesamiento de SonarQube. El Pipeline script actual ya contiene el análisis
y no necesita cambiarse para esta corrección del controlador.
