# Análisis SAST con Semgrep

Semgrep **1.181.0** con `--config=auto` (reglas de la comunidad para Java, HTML, JavaScript, Dockerfile y YAML). Se ejecuta en el stage `Security Scan (SAST) - Semgrep` del [Jenkinsfile](../Jenkinsfile), que archiva el reporte `reportes/semgrep.json` en cada build.

## Alcance y exclusiones

| Excluido | Justificación |
|---|---|
| `target/` | Código compilado y reportes generados por Maven. |
| `evidencias/` | Reportes HTML de JaCoCo conservados como evidencia; no es código de la aplicación. |
| `*.min.js`, `*.min.css`, `bootstrap*` | Bootstrap 5 incluido como librería de terceros; sus hallazgos no son corregibles en este repositorio. |

Se analizan 41 archivos: código Java, plantillas Thymeleaf, JavaScript propio, CSS propio, Dockerfile y configuración.

## Hallazgos (21/11/2026)

| # | Regla | Severidad | Archivo | Clasificación | Acción |
|---|---|---|---|---|---|
| 1 | `html.security.audit.missing-integrity` | WARNING | `templates/home.html:13` | Verdadero positivo | Corregido |
| 2 | `html.security.audit.missing-integrity` | WARNING | `templates/proyecto.html:9` | Verdadero positivo | Corregido |

**Problema.** Las hojas de estilo de Font Awesome 5.13.0 (cdnjs) y Bootstrap Icons 1.11.3 (jsDelivr) se cargaban sin *Subresource Integrity*. Si el CDN fuera comprometido, el navegador aplicaría un archivo modificado sin advertirlo.

**Corrección.** Se calculó el hash SHA-384 de cada archivo publicado y se agregaron los atributos `integrity="sha384-…"` y `crossorigin="anonymous"`. Si el contenido del CDN cambia, el navegador rechaza el archivo.

**Nueva ejecución:** 0 hallazgos.

## No es un hallazgo

- **Google Fonts** (`inicio.html`) no lleva SRI: el servidor genera un CSS distinto según el navegador, así que no existe un hash fijo. Semgrep no lo reporta.
- **Error de análisis en `mvnw`.** Semgrep no puede interpretar una construcción `case … esac` del script del Maven Wrapper. Es un script generado por Maven, no código del proyecto. Es una limitación del analizador, no una vulnerabilidad.
