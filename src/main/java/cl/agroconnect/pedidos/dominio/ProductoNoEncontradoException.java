package cl.agroconnect.pedidos.dominio;

public class ProductoNoEncontradoException extends NegocioException {
    public ProductoNoEncontradoException(String dato) {
        super("Producto no encontrado: " + dato);
    }
}
