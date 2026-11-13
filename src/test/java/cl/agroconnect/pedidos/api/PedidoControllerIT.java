package cl.agroconnect.pedidos.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Prueba de integración: API REST /api/pedidos")
class PedidoControllerIT {

    @Autowired
    private MockMvc mvc;

    private static final String PEDIDO_OK = """
            {"idCliente":"C-001","items":[{"codigoProducto":"P-001","cantidad":2}],
             "modalidadDespacho":"EXPRESS","transportista":"RUTASUR","direccionEntrega":"Talca"}
            """;

    @Test
    @DisplayName("POST registra el pedido y responde 201 con seguimiento")
    void registraPedido() throws Exception {
        mvc.perform(post("/api/pedidos").contentType(MediaType.APPLICATION_JSON).content(PEDIDO_OK))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/pedidos/1"))
                .andExpect(jsonPath("$.estado").value("DESPACHO_SOLICITADO"))
                .andExpect(jsonPath("$.numeroSeguimiento", startsWith("RS-")))
                .andExpect(jsonPath("$.tarifaDespacho").value(21000))
                .andExpect(jsonPath("$.total").value(71000));

        mvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lineas[0].codigoProducto").value("P-001"));
    }

    @Test
    @DisplayName("Cliente inactivo responde 422")
    void clienteInactivo() throws Exception {
        mvc.perform(post("/api/pedidos").contentType(MediaType.APPLICATION_JSON)
                        .content(PEDIDO_OK.replace("C-001", "C-003")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.detail", startsWith("Cliente no válido")));
    }

    @Test
    @DisplayName("Falta de stock responde 422")
    void sinStock() throws Exception {
        mvc.perform(post("/api/pedidos").contentType(MediaType.APPLICATION_JSON)
                        .content(PEDIDO_OK.replace("\"P-001\",\"cantidad\":2", "\"P-003\",\"cantidad\":50")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.detail", startsWith("Stock insuficiente")));
    }

    @Test
    @DisplayName("Solicitud sin campos obligatorios responde 400")
    void solicitudInvalida() throws Exception {
        mvc.perform(post("/api/pedidos").contentType(MediaType.APPLICATION_JSON).content("{\"items\":[]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Pedido inexistente responde 404")
    void pedidoInexistente() throws Exception {
        mvc.perform(get("/api/pedidos/999")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Actuator expone liveness y readiness para Kubernetes")
    void health() throws Exception {
        mvc.perform(get("/actuator/health/liveness")).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
        mvc.perform(get("/actuator/health/readiness")).andExpect(status().isOk());
    }
}
