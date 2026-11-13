package cl.agroconnect.pedidos.aplicacion;

/**
 * Patrón Adapter: interfaz objetivo (Target) común para todos los operadores logísticos.
 * Cada transportista externo se integra con un adaptador que implementa esta interfaz (RNF-05).
 */
public interface TransportistaPort {

    String codigo();

    ConfirmacionDespacho solicitarDespacho(SolicitudDespacho solicitud);
}
