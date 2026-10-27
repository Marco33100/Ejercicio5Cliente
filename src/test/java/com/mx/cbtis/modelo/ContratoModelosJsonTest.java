package com.mx.cbtis.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.databind.ObjectMapper;

class ContratoModelosJsonTest {
    private final ObjectMapper mapper = new ObjectMapper();

    static Stream<Arguments> contratos() {
        return Stream.of(
                Arguments.of(new Asignaciones(34, 45, 23, "2026-10-21"), """
                    {"id_asignacion":34,"id_usuario":45,"id_tarea":23,"fecha_asignacion":"2026-10-21"}
                    """),
                Arguments.of(new Comentarios(56, "45", 23, "Pruebas listas", "2026-10-21T17:00:00"), """
                    {"id_comentario":56,"id_usuario":"45","id_tarea":23,
                     "texto_comentario":"Pruebas listas","fecha_hora":"2026-10-21T17:00:00"}
                    """),
                Arguments.of(new Proyectos(12, "Entrega", "Version Java 21", "2026-10-21",
                        "2026-10-30", "En progreso", "fondo.png"), """
                    {"id_proyecto":12,"nombre":"Entrega","descripcion":"Version Java 21",
                     "fecha_inicio":"2026-10-21","fecha_finalizacion":"2026-10-30",
                     "estado_proyecto":"En progreso","fondo":"fondo.png"}
                    """),
                Arguments.of(new Tareas(23, "Compilar", "Verificar API", "2026-10-25", "Sprint 1", "Pendiente", 12), """
                    {"id_tarea":23,"nombre":"Compilar","descripcion":"Verificar API",
                     "fecha_vencimiento":"2026-10-25","lista":"Sprint 1","estado":"Pendiente","id_proyecto":12}
                    """),
                Arguments.of(new Usuario(45, "Prueba", "prueba@example.test", "valor-de-prueba", "estudiante", "perfil.png"), """
                    {"id_usuario":45,"nombre":"Prueba","correo_electronico":"prueba@example.test",
                     "contrasena":"valor-de-prueba","rol":"estudiante","imagen_usuario":"perfil.png"}
                    """));
    }

    @ParameterizedTest
    @MethodSource("contratos")
    void contratoJsonConservaLosNombresTiposYDatosEnAmbasDirecciones(Object registro, String json) throws Exception {
        assertEquals(mapper.readTree(json), mapper.valueToTree(registro));
        Object recibido = mapper.readValue(json, registro.getClass());
        assertEquals(mapper.readTree(json), mapper.valueToTree(recibido));
    }
}
