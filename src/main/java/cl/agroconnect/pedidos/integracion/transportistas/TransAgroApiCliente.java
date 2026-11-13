package cl.agroconnect.pedidos.integracion.transportistas;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Adaptee: simula el cliente de la API de la empresa TransAgro.
 * Su interfaz es incompatible con AgroConnect: recibe y devuelve texto XML.
 */
public class TransAgroApiCliente {

    private final AtomicInteger secuencia = new AtomicInteger(1000);

    public String crearOrdenEnvio(String xmlOrden) {
        int numero = secuencia.incrementAndGet();
        return "<respuesta><tracking>TA-" + numero + "</tracking><fechaRetiro>"
                + LocalDate.now().plusDays(1) + "</fechaRetiro></respuesta>";
    }
}
