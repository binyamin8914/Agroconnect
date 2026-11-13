package cl.agroconnect.pedidos.integracion.transportistas;

import cl.agroconnect.pedidos.aplicacion.ConfirmacionDespacho;
import cl.agroconnect.pedidos.aplicacion.SolicitudDespacho;
import cl.agroconnect.pedidos.aplicacion.TransportistaPort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

/** Adapter: traduce la interfaz común TransportistaPort a la API de RutaSur (gramos y mapa de respuesta). */
@Component
public class RutaSurAdapter implements TransportistaPort {

    private final RutaSurApiCliente api;

    public RutaSurAdapter() {
        this(new RutaSurApiCliente());
    }

    RutaSurAdapter(RutaSurApiCliente api) {
        this.api = api;
    }

    @Override
    public String codigo() {
        return "RUTASUR";
    }

    @Override
    public ConfirmacionDespacho solicitarDespacho(SolicitudDespacho solicitud) {
        int gramos = (int) Math.round(solicitud.pesoKg() * 1000);
        Map<String, Object> respuesta = api.despachar(solicitud.direccionEntrega(), gramos,
                "AGRO-" + solicitud.idPedido());
        int dias = (Integer) respuesta.get("diasHabiles");
        return new ConfirmacionDespacho(codigo(), (String) respuesta.get("guia"), LocalDate.now().plusDays(dias));
    }
}
