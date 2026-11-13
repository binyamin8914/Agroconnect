package cl.agroconnect.pedidos.integracion.transportistas;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Adaptee: simula el cliente de la API de la empresa RutaSur.
 * Usa otra firma: destino, peso en gramos y referencia; responde un mapa con "guia" y "diasHabiles".
 */
public class RutaSurApiCliente {

    private final AtomicInteger secuencia = new AtomicInteger(500);

    public Map<String, Object> despachar(String destino, int pesoGramos, String referencia) {
        return Map.of("guia", "RS-" + secuencia.incrementAndGet(), "diasHabiles", 2);
    }
}
