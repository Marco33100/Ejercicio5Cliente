package com.mx.cbtis.controller;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.stream.Stream;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

class GestorControllerTest {
    private static final String API = "http://localhost:8081";
    private static final String PROYECTO = """
        {"id_proyecto":12,"nombre":"Entrega Java 21","descripcion":"Migracion del gestor",
         "fecha_inicio":"2026-10-21","fecha_finalizacion":"2026-10-30",
         "estado_proyecto":"En progreso","fondo":"proyecto.png"}
        """;
    private static final String TAREA = """
        {"id_tarea":23,"nombre":"Compilar API","descripcion":"Verificar las pruebas",
         "fecha_vencimiento":"2026-10-25","lista":"Sprint 1","estado":"Pendiente","id_proyecto":12}
        """;
    private static final String COMENTARIO = """
        {"id_comentario":56,"id_usuario":"45","id_tarea":23,
         "texto_comentario":"Pruebas listas","fecha_hora":"2026-10-21T17:00:00"}
        """;
    private static final String USUARIO = """
        {"id_usuario":45,"nombre":"Usuario de prueba","correo_electronico":"prueba@example.test",
         "contrasena":"valor-de-prueba","rol":"estudiante","imagen_usuario":"perfil.png"}
        """;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private MockRestServiceServer api;

    @BeforeEach
    void prepararClienteConApiSimulada() {
        RestTemplate template = new RestTemplate();
        api = MockRestServiceServer.bindTo(template).build();
        mvc = MockMvcBuilders.standaloneSetup(new GestorController(template))
                .setViewResolvers(new InternalResourceViewResolver("/templates/", ".html"))
                .build();
    }

    @AfterEach
    void verificarQueSeHicieronLasPeticionesEsperadas() {
        api.verify();
    }

    @ParameterizedTest
    @CsvSource({"/,home", "/tutorial,tutorial", "/manual,manual", "/login,login"})
    void paginasPublicasSeleccionanUnaPlantillaExistente(String ruta, String plantilla) throws Exception {
        mvc.perform(get(ruta)).andExpect(status().isOk()).andExpect(view().name(plantilla));
        assertTrue(new ClassPathResource("templates/" + plantilla + ".html").exists());
    }

    @ParameterizedTest
    @CsvSource({"/register,register,usuario", "/inicio,inicio,proyectos", "/tarea,proyecto,tareas"})
    void formulariosPreparanElModeloDeLaVista(String ruta, String plantilla, String atributo) throws Exception {
        mvc.perform(get(ruta)).andExpect(status().isOk()).andExpect(view().name(plantilla))
                .andExpect(model().attributeExists(atributo));
    }

    static Stream<Arguments> formularios() {
        return Stream.of(
                Arguments.of("/saveUsuario", "/addUsuario", "/login", USUARIO),
                Arguments.of("/saveProyecto", "/addProyecto", "/inicio", PROYECTO),
                Arguments.of("/saveTarea", "/addTareas", "/proyecto?id_proyecto=12", TAREA));
    }

    @ParameterizedTest
    @MethodSource("formularios")
    void guardarFormularioEnviaSusDatosYRedirige(String ruta, String backend, String destino, String json)
            throws Exception {
        api.expect(requestTo(API + backend)).andExpect(method(HttpMethod.POST))
                .andExpect(content().json(json, true)).andRespond(withSuccess("1", MediaType.APPLICATION_JSON));
        MockHttpServletRequestBuilder peticion = post(ruta).contentType(MediaType.APPLICATION_FORM_URLENCODED);
        mapper.readTree(json).fields().forEachRemaining(campo -> peticion.param(campo.getKey(), campo.getValue().asText()));
        mvc.perform(peticion).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl(destino));
    }

    static Stream<Arguments> consultas() {
        return Stream.of(
                Arguments.of("/consultarProyectos", "/buscarProyectos", null, PROYECTO),
                Arguments.of("/consultarUsuarios", "/buscarUsuarios", null, USUARIO),
                Arguments.of("/consultarTareas", "/buscarTareas", null, TAREA),
                Arguments.of("/consultarComentarios", "/buscarComentarios", null, COMENTARIO),
                Arguments.of("/obtenerTareas", "/getTarea", TAREA, TAREA),
                Arguments.of("/obtenerComentarios", "/getComentario", COMENTARIO, COMENTARIO));
    }

    @ParameterizedTest
    @MethodSource("consultas")
    void consultasDevuelvenLosRegistrosDeLaApi(String ruta, String backend, String entrada, String registro)
            throws Exception {
        var expectativa = api.expect(requestTo(API + backend)).andExpect(method(HttpMethod.POST));
        if (entrada != null) {
            expectativa.andExpect(content().json(entrada, true));
        }
        expectativa.andRespond(withSuccess("[" + registro + "]", MediaType.APPLICATION_JSON));
        MockHttpServletRequestBuilder peticion = post(ruta).contentType(MediaType.APPLICATION_JSON);
        if (entrada != null) {
            peticion.content(entrada);
        }
        mvc.perform(peticion).andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .json("[" + registro + "]", true));
    }

    @Test
    void consultaSinRegistrosDevuelveUnaListaVacia() throws Exception {
        api.expect(requestTo(API + "/buscarProyectos")).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));
        mvc.perform(post("/consultarProyectos")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void proyectoPreparaFormularioYUsuariosDisponibles() throws Exception {
        api.expect(requestTo(API + "/buscarUsuarios")).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("[" + USUARIO + "]", MediaType.APPLICATION_JSON));
        mvc.perform(get("/proyecto")).andExpect(status().isOk()).andExpect(view().name("proyecto"))
                .andExpect(model().attributeExists("tarea", "comentarios"))
                .andExpect(model().attribute("listaUsuarios", org.hamcrest.Matchers.hasSize(1)));
    }

    static Stream<Arguments> busquedas() {
        return Stream.of(
                Arguments.of("/actualizarProyecto", "/buscarProyecto?id_proyecto=12", PROYECTO),
                Arguments.of("/actualizarTarea", "/buscarTarea?id_tarea=23", TAREA));
    }

    @ParameterizedTest
    @MethodSource("busquedas")
    void editarBuscaEnLaApiElIdentificadorSolicitado(String ruta, String backend, String json) throws Exception {
        api.expect(requestTo(API + backend)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
        mvc.perform(post(ruta).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().json(json, true));
    }

    static Stream<Arguments> eliminaciones() {
        return Stream.of(
                Arguments.of("/eliminarProyecto", "/deleteProyecto", "/inicio", PROYECTO),
                Arguments.of("/eliminarTarea", "/deleteTareas", "/proyecto", TAREA));
    }

    @ParameterizedTest
    @MethodSource("eliminaciones")
    void eliminarEnviaDeleteConElRegistroCorrecto(String ruta, String backend, String destino, String json)
            throws Exception {
        api.expect(requestTo(API + backend)).andExpect(method(HttpMethod.DELETE))
                .andExpect(content().json(json, true)).andRespond(withSuccess("1", MediaType.APPLICATION_JSON));
        mvc.perform(post(ruta).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl(destino));
    }

    @Test
    void loginEnviaLasCredencialesYDevuelveElPerfilRecibido() throws Exception {
        api.expect(requestTo(API + "/getUsuarioLogin")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json(USUARIO, true)).andRespond(withSuccess(USUARIO, MediaType.APPLICATION_JSON));
        mvc.perform(post("/verificarLogin").contentType(MediaType.APPLICATION_JSON).content(USUARIO))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id_usuario").value(45))
                .andExpect(jsonPath("$.nombre").value("Usuario de prueba"));
    }

    @Test
    void guardarComentarioEnviaSuContenidoYSeleccionaLaVistaProyecto() throws Exception {
        api.expect(requestTo(API + "/addComentario")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json(COMENTARIO, true)).andRespond(withSuccess("1", MediaType.APPLICATION_JSON));
        mvc.perform(post("/saveComentario").contentType(MediaType.APPLICATION_JSON).content(COMENTARIO))
                .andExpect(status().isOk()).andExpect(view().name("proyecto"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/verificarTarea", "/cambiosTareas"})
    void accionesDeTareaDevuelvenLaConfirmacionDeLaApi(String ruta) throws Exception {
        api.expect(requestTo(API + "/addTareas")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json(TAREA, true)).andRespond(withSuccess("1", MediaType.APPLICATION_JSON));
        mvc.perform(post(ruta).contentType(MediaType.APPLICATION_JSON).content(TAREA))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string("1"));
    }

    @Test
    void errorDelBackendNoSePresentaComoUnaConsultaExitosa() {
        api.expect(requestTo(API + "/buscarProyectos")).andRespond(withServerError());
        ServletException error = assertThrows(ServletException.class,
                () -> mvc.perform(post("/consultarProyectos")));
        assertInstanceOf(HttpServerErrorException.class, error.getCause());
    }
}
