package cl.agroconnect.pedidos.dominio;

public class ClienteNoValidoException extends NegocioException {
    public ClienteNoValidoException(String dato) {
        super("Cliente no válido o inactivo: " + dato);
    }
}
