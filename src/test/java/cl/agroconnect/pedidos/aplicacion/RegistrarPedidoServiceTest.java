package cl.agroconnect.pedidos.aplicacion;

import cl.agroconnect.pedidos.dominio.ClienteNoValidoException;
import cl.agroconnect.pedidos.dominio.EstadoPedido;
import cl.agroconnect.pedidos.dominio.NegocioException;
import cl.agroconnect.pedidos.dominio.Pedido;
import cl.agroconnect.pedidos.dominio.PedidoNoEncontradoException;
import cl.agroconnect.pedidos.dominio.ProductoNoEncontradoException;
import cl.agroconnect.pedidos.dominio.StockInsuficienteException;
import cl.agroconnect.pedidos.dominio.evento.PublicadorEventosPedido;
import cl.agroconnect.pedidos.dominio.tarifa.CalculadoraTarifa;
import cl.agroconnect.pedidos.dominio.tarifa.TarifaEstandar;
import cl.agroconnect.pedidos.dominio.tarifa.TarifaExpress;
import cl.agroconnect.pedidos.dominio.tarifa.TarifaRetiroEnBodega;
import cl.agroconnect.pedidos.infraestructura.ClienteRepositoryEnMemoria;
import cl.agroconnect.pedidos.infraestructura.InventarioRepositoryEnMemoria;
import cl.agroconnect.pedidos.infraestructura.PedidoRepositoryEnMemoria;
import cl.agroconnect.pedidos.integracion.transportistas.RutaSurAdapter;
import cl.agroconnect.pedidos.integracion.transportistas.TransAgroAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Caso de uso RF-04: Registrar pedido")
class RegistrarPedidoServiceTest {

    private InventarioRepositoryEnMemoria inventario;
    private NotificacionClienteObserver notificaciones;
    private RegistrarPedidoService servicio;

    @BeforeEach
    void preparar() {
        inventario = new InventarioRepositoryEnMemoria();
        RegistroTransportistas transportistas = new RegistroTransportistas(
                List.of(new TransAgroAdapter(), new RutaSurAdapter()));
        notificaciones = new NotificacionClienteObserver();
        PublicadorEventosPedido publicador = new PublicadorEventosPedido(
                List.of(new SolicitudDespachoObserver(transportistas), notificaciones));
        servicio = new RegistrarPedidoService(new ClienteRepositoryEnMemoria(), inventario,
                new PedidoRepositoryEnMemoria(),
                new CalculadoraTarifa(List.of(new TarifaEstandar(), new TarifaExpress(), new TarifaRetiroEnBodega())),
                transportistas, publicador);
    }

    private ComandoRegistrarPedido comando(String cliente, String producto, int cantidad, String modalidad, String transp) {
        return new ComandoRegistrarPedido(cliente, List.of(new ComandoRegistrarPedido.Item(producto, cantidad)),
                modalidad, transp, "Talca");
    }

    @Test
    @DisplayName("Registra el pedido, reserva stock, solicita despacho y notifica al cliente")
    void flujoExitoso() {
        Pedido pedido = servicio.registrar(comando("C-001", "P-001", 2, "ESTANDAR", "TRANSAGRO"));

        assertThat(pedido.getId()).isEqualTo(1L);
        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.DESPACHO_SOLICITADO);
        assertThat(pedido.getNumeroSeguimiento()).startsWith("TA-");
        assertThat(pedido.getTarifaDespacho()).isEqualByComparingTo("10500");
        assertThat(inventario.stockDisponible("P-001")).isEqualTo(98);
        assertThat(notificaciones.notificacionesEnviadas()).hasSize(1);
        assertThat(servicio.consultar(1L)).isSameAs(pedido);
    }

    @Test
    @DisplayName("Retiro en bodega no requiere transportista y queda listo para retiro")
    void retiroEnBodega() {
        Pedido pedido = servicio.registrar(comando("C-002", "P-002", 1, "RETIRO_EN_BODEGA", null));

        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.LISTO_PARA_RETIRO);
        assertThat(pedido.total()).isEqualByComparingTo("48000");
    }

    @Test
    @DisplayName("Rechaza clientes inexistentes o inactivos")
    void clienteNoValido() {
        assertThatThrownBy(() -> servicio.registrar(comando("C-003", "P-001", 1, "ESTANDAR", "TRANSAGRO")))
                .isInstanceOf(ClienteNoValidoException.class);
        assertThatThrownBy(() -> servicio.registrar(comando("C-999", "P-001", 1, "ESTANDAR", "TRANSAGRO")))
                .isInstanceOf(ClienteNoValidoException.class);
    }

    @Test
    @DisplayName("Rechaza falta de stock sin reservar nada")
    void sinStock() {
        assertThatThrownBy(() -> servicio.registrar(comando("C-001", "P-003", 4, "ESTANDAR", "RUTASUR")))
                .isInstanceOf(StockInsuficienteException.class);
        assertThat(inventario.stockDisponible("P-003")).isEqualTo(3);
        assertThat(notificaciones.notificacionesEnviadas()).isEmpty();
    }

    @Test
    @DisplayName("Rechaza productos inexistentes y transportistas no integrados")
    void productoYTransportistaInvalidos() {
        assertThatThrownBy(() -> servicio.registrar(comando("C-001", "P-404", 1, "ESTANDAR", "TRANSAGRO")))
                .isInstanceOf(ProductoNoEncontradoException.class);
        assertThatThrownBy(() -> servicio.registrar(comando("C-001", "P-001", 1, "ESTANDAR", "CHILEXPRESS")))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("CHILEXPRESS");
        assertThatThrownBy(() -> servicio.consultar(99L)).isInstanceOf(PedidoNoEncontradoException.class);
    }

    @Test
    @DisplayName("El registro de transportistas rechaza códigos desconocidos")
    void registroTransportistas() {
        RegistroTransportistas registro = new RegistroTransportistas(List.of(new RutaSurAdapter()));
        assertThat(registro.existe("RUTASUR")).isTrue();
        assertThatThrownBy(() -> registro.obtener("TRANSAGRO")).isInstanceOf(NegocioException.class);
    }

    @Test
    @DisplayName("La reserva concurrente no deja stock negativo")
    void reservaNoDejaStockNegativo() {
        assertThatThrownBy(() -> inventario.reservar("P-003", 10)).isInstanceOf(StockInsuficienteException.class);
        assertThat(inventario.stockDisponible("P-003")).isEqualTo(3);
    }
}
