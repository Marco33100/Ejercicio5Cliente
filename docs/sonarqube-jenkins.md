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
