package cl.agroconnect.pedidos.dominio.evento;

/** Patrón Observer: interfaz de los interesados en el registro de un pedido. */
public interface PedidoObserver {

    void alRegistrarPedido(PedidoRegistradoEvento evento);
}
