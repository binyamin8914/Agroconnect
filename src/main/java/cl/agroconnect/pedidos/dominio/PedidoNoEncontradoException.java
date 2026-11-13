package cl.agroconnect.pedidos.dominio;

public class PedidoNoEncontradoException extends NegocioException {
    public PedidoNoEncontradoException(String dato) {
        super("Pedido no encontrado: " + dato);
    }
}
