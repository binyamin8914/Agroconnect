package cl.agroconnect.pedidos.dominio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Entidad Pedido")
class PedidoTest {

    private final Producto fertilizante = new Producto("P-001", "Fertilizante", new BigDecimal("25000"), 25.0);
    private final Producto semilla = new Producto("P-002", "Semilla", new BigDecimal("48000"), 10.0);

    @Test
    @DisplayName("Calcula subtotal, peso y total con la tarifa de despacho")
    void calculaTotales() {
        Pedido pedido = new Pedido(1L, "C-001",
                List.of(new LineaPedido(fertilizante, 2), new LineaPedido(semilla, 1)),
                "ESTANDAR", "TRANSAGRO", "Talca", new BigDecimal("12000"));

        assertThat(pedido.subtotalProductos()).isEqualByComparingTo("98000");
        assertThat(pedido.pesoTotalKg()).isEqualTo(60.0);
        assertThat(pedido.total()).isEqualByComparingTo("110000");
        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.REGISTRADO);
    }

    @Test
    @DisplayName("Cambia de estado al solicitar despacho")
    void cambiaEstado() {
        Pedido pedido = new Pedido(1L, "C-001", List.of(new LineaPedido(fertilizante, 1)),
                "ESTANDAR", "TRANSAGRO", "Talca", BigDecimal.ZERO);
        pedido.marcarDespachoSolicitado("TA-1");

        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.DESPACHO_SOLICITADO);
        assertThat(pedido.getNumeroSeguimiento()).isEqualTo("TA-1");
    }

    @Test
    @DisplayName("No permite pedidos vacíos ni cantidades inválidas")
    void validaciones() {
        assertThatThrownBy(() -> new Pedido(1L, "C-001", List.of(), "ESTANDAR", "TRANSAGRO", "Talca", BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new LineaPedido(fertilizante, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
