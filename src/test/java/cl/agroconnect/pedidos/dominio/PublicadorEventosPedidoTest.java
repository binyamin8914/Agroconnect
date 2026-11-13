package cl.agroconnect.pedidos.dominio;

import cl.agroconnect.pedidos.dominio.evento.PedidoObserver;
import cl.agroconnect.pedidos.dominio.evento.PedidoRegistradoEvento;
import cl.agroconnect.pedidos.dominio.evento.PublicadorEventosPedido;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Patrón Observer: publicación del evento PedidoRegistrado")
class PublicadorEventosPedidoTest {

    private Pedido pedidoDePrueba() {
        Producto p = new Producto("P-001", "Fertilizante", new BigDecimal("1000"), 1.0);
        return new Pedido(1L, "C-001", List.of(new LineaPedido(p, 2)), "ESTANDAR", "TRANSAGRO",
                "Talca", BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Todos los observadores suscritos reciben el evento")
    void notificaATodos() {
        List<String> recibidos = new ArrayList<>();
        PedidoObserver despacho = e -> recibidos.add("despacho:" + e.pedido().getId());
        PedidoObserver notificacion = e -> recibidos.add("notificacion:" + e.pedido().getId());
        PublicadorEventosPedido publicador = new PublicadorEventosPedido(List.of(despacho));
        publicador.suscribir(notificacion);

        publicador.notificar(new PedidoRegistradoEvento(pedidoDePrueba()));

        assertThat(recibidos).containsExactly("despacho:1", "notificacion:1");
    }

    @Test
    @DisplayName("Un observador desuscrito deja de recibir eventos")
    void desuscribir() {
        List<String> recibidos = new ArrayList<>();
        PedidoObserver observador = e -> recibidos.add("x");
        PublicadorEventosPedido publicador = new PublicadorEventosPedido(List.of());
        publicador.suscribir(observador);
        publicador.desuscribir(observador);

        publicador.notificar(new PedidoRegistradoEvento(pedidoDePrueba()));

        assertThat(recibidos).isEmpty();
        assertThat(publicador.cantidadObservadores()).isZero();
    }
}
