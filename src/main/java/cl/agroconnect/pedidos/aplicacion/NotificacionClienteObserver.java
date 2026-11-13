package cl.agroconnect.pedidos.aplicacion;

import cl.agroconnect.pedidos.dominio.Pedido;
import cl.agroconnect.pedidos.dominio.evento.PedidoObserver;
import cl.agroconnect.pedidos.dominio.evento.PedidoRegistradoEvento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Observador concreto: genera la confirmación del pedido para el cliente (correo / notificación). */
@Component
@Order(2)
public class NotificacionClienteObserver implements PedidoObserver {

    private static final Logger log = LoggerFactory.getLogger(NotificacionClienteObserver.class);

    private final List<String> enviadas = new CopyOnWriteArrayList<>();

    @Override
    public void alRegistrarPedido(PedidoRegistradoEvento evento) {
        Pedido pedido = evento.pedido();
        String mensaje = "Pedido " + pedido.getId() + " registrado para cliente " + pedido.getIdCliente()
                + " (estado " + pedido.getEstado() + ")";
        enviadas.add(mensaje);
        log.info("Notificación enviada: {}", mensaje);
    }

    public List<String> notificacionesEnviadas() {
        return List.copyOf(enviadas);
    }
}
