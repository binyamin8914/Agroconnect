package cl.agroconnect.pedidos.aplicacion;

import cl.agroconnect.pedidos.dominio.ClienteNoValidoException;
import cl.agroconnect.pedidos.dominio.LineaPedido;
import cl.agroconnect.pedidos.dominio.NegocioException;
import cl.agroconnect.pedidos.dominio.Pedido;
import cl.agroconnect.pedidos.dominio.PedidoNoEncontradoException;
import cl.agroconnect.pedidos.dominio.Producto;
import cl.agroconnect.pedidos.dominio.ProductoNoEncontradoException;
import cl.agroconnect.pedidos.dominio.StockInsuficienteException;
import cl.agroconnect.pedidos.dominio.evento.PedidoRegistradoEvento;
import cl.agroconnect.pedidos.dominio.evento.PublicadorEventosPedido;
import cl.agroconnect.pedidos.dominio.tarifa.CalculadoraTarifa;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Caso de uso RF-04 "Registrar pedido".
 * Valida cliente, verifica y reserva stock, calcula la tarifa (Strategy),
 * guarda el pedido y publica el evento PedidoRegistrado (Observer).
 */
@Service
public class RegistrarPedidoService {

    static final String RETIRO_EN_BODEGA = "RETIRO_EN_BODEGA";

    private final ClienteRepository clientes;
    private final InventarioRepository inventario;
    private final PedidoRepository pedidos;
    private final CalculadoraTarifa calculadoraTarifa;
    private final RegistroTransportistas transportistas;
    private final PublicadorEventosPedido publicador;

    public RegistrarPedidoService(ClienteRepository clientes, InventarioRepository inventario,
                                  PedidoRepository pedidos, CalculadoraTarifa calculadoraTarifa,
                                  RegistroTransportistas transportistas, PublicadorEventosPedido publicador) {
        this.clientes = clientes;
        this.inventario = inventario;
        this.pedidos = pedidos;
        this.calculadoraTarifa = calculadoraTarifa;
        this.transportistas = transportistas;
        this.publicador = publicador;
    }

    public Pedido registrar(ComandoRegistrarPedido comando) {
        validarCliente(comando.idCliente());
        validarTransportista(comando);

        List<LineaPedido> lineas = new ArrayList<>();
        for (ComandoRegistrarPedido.Item item : comando.items()) {
            Producto producto = inventario.buscarProducto(item.codigoProducto())
                    .orElseThrow(() -> new ProductoNoEncontradoException(item.codigoProducto()));
            if (inventario.stockDisponible(producto.codigo()) < item.cantidad()) {
                throw new StockInsuficienteException(producto.codigo());
            }
            lineas.add(new LineaPedido(producto, item.cantidad()));
        }
        lineas.forEach(l -> inventario.reservar(l.producto().codigo(), l.cantidad()));

        double peso = lineas.stream().mapToDouble(LineaPedido::pesoKg).sum();
        BigDecimal tarifa = calculadoraTarifa.calcular(comando.modalidadDespacho(), peso);

        Pedido pedido = new Pedido(pedidos.siguienteId(), comando.idCliente(), lineas,
                comando.modalidadDespacho(), comando.transportista(), comando.direccionEntrega(), tarifa);
        pedidos.guardar(pedido);

        publicador.notificar(new PedidoRegistradoEvento(pedido));
        return pedido;
    }

    public Pedido consultar(Long id) {
        return pedidos.buscarPorId(id).orElseThrow(() -> new PedidoNoEncontradoException(String.valueOf(id)));
    }

    private void validarCliente(String idCliente) {
        clientes.buscarPorId(idCliente)
                .filter(c -> c.activo())
                .orElseThrow(() -> new ClienteNoValidoException(idCliente));
    }

    private void validarTransportista(ComandoRegistrarPedido comando) {
        if (RETIRO_EN_BODEGA.equals(comando.modalidadDespacho())) {
            return;
        }
        if (comando.transportista() == null || !transportistas.existe(comando.transportista())) {
            throw new NegocioException("Transportista no integrado: " + comando.transportista());
        }
    }
}
