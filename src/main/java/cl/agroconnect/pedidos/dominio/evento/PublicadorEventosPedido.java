package cl.agroconnect.pedidos.dominio.evento;

import java.util.ArrayList;
import java.util.List;

/**
 * Sujeto del patrón Observer. El servicio de pedidos solo publica el evento;
 * no conoce a Despacho ni a Notificaciones, que se suscriben como observadores.
 */
public class PublicadorEventosPedido {

    private final List<PedidoObserver> observadores = new ArrayList<>();

    public PublicadorEventosPedido(List<PedidoObserver> observadoresIniciales) {
        observadores.addAll(observadoresIniciales);
    }

    public void suscribir(PedidoObserver observador) {
        observadores.add(observador);
    }

    public void desuscribir(PedidoObserver observador) {
        observadores.remove(observador);
    }

    public void notificar(PedidoRegistradoEvento evento) {
        observadores.forEach(o -> o.alRegistrarPedido(evento));
    }

    public int cantidadObservadores() {
        return observadores.size();
    }
}
