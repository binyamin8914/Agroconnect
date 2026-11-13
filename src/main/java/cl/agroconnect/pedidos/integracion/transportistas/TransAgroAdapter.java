package cl.agroconnect.pedidos.integracion.transportistas;

import cl.agroconnect.pedidos.aplicacion.ConfirmacionDespacho;
import cl.agroconnect.pedidos.aplicacion.SolicitudDespacho;
import cl.agroconnect.pedidos.aplicacion.TransportistaPort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/** Adapter: traduce la interfaz común TransportistaPort al formato XML de TransAgro. */
@Component
public class TransAgroAdapter implements TransportistaPort {

    private final TransAgroApiCliente api;

    public TransAgroAdapter() {
        this(new TransAgroApiCliente());
    }

    TransAgroAdapter(TransAgroApiCliente api) {
        this.api = api;
    }

    @Override
    public String codigo() {
        return "TRANSAGRO";
    }

    @Override
    public ConfirmacionDespacho solicitarDespacho(SolicitudDespacho solicitud) {
        String xml = "<orden><referencia>" + solicitud.idPedido() + "</referencia><destino>"
                + solicitud.direccionEntrega() + "</destino><kilos>" + solicitud.pesoKg() + "</kilos></orden>";
        String respuesta = api.crearOrdenEnvio(xml);
        return new ConfirmacionDespacho(codigo(), extraer(respuesta, "tracking"),
                LocalDate.parse(extraer(respuesta, "fechaRetiro")));
    }

    private static String extraer(String xml, String etiqueta) {
        int inicio = xml.indexOf("<" + etiqueta + ">") + etiqueta.length() + 2;
        int fin = xml.indexOf("</" + etiqueta + ">");
        return xml.substring(inicio, fin);
    }
}
