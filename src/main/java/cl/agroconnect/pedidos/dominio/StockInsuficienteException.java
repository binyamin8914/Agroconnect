package cl.agroconnect.pedidos.dominio;

public class StockInsuficienteException extends NegocioException {
    public StockInsuficienteException(String dato) {
        super("Stock insuficiente para el producto: " + dato);
    }
}
