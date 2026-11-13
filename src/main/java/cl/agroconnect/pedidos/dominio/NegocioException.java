package cl.agroconnect.pedidos.dominio;

/** Error de regla de negocio (cliente no válido, sin stock, etc.). */
public class NegocioException extends RuntimeException {
    public NegocioException(String mensaje) {
        super(mensaje);
    }
}
