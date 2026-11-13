package cl.agroconnect.pedidos.integracion.transportistas;

import cl.agroconnect.pedidos.aplicacion.ConfirmacionDespacho;
import cl.agroconnect.pedidos.aplicacion.SolicitudDespacho;
import cl.agroconnect.pedidos.aplicacion.TransportistaPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Patrón Adapter: integración con transportistas")
class TransportistaAdaptersTest {

    private final SolicitudDespacho solicitud = new SolicitudDespacho(7L, "Camino a Pelarco km 3, Talca", 52.5);

    @Test
    @DisplayName("TransAgroAdapter traduce la respuesta XML a ConfirmacionDespacho")
    void transAgro() {
        TransportistaPort port = new TransAgroAdapter(new TransAgroApiCliente());

        ConfirmacionDespacho c = port.solicitarDespacho(solicitud);

        assertThat(port.codigo()).isEqualTo("TRANSAGRO");
        assertThat(c.numeroSeguimiento()).startsWith("TA-");
        assertThat(c.fechaProgramada()).isEqualTo(LocalDate.now().plusDays(1));
    }

    @Test
    @DisplayName("RutaSurAdapter convierte kg a gramos y la respuesta en mapa a ConfirmacionDespacho")
    void rutaSur() {
        int[] gramosRecibidos = new int[1];
        RutaSurApiCliente api = new RutaSurApiCliente() {
            @Override
            public java.util.Map<String, Object> despachar(String destino, int pesoGramos, String referencia) {
                gramosRecibidos[0] = pesoGramos;
                return super.despachar(destino, pesoGramos, referencia);
            }
        };
        TransportistaPort port = new RutaSurAdapter(api);

        ConfirmacionDespacho c = port.solicitarDespacho(solicitud);

        assertThat(gramosRecibidos[0]).isEqualTo(52500);
        assertThat(c.transportista()).isEqualTo("RUTASUR");
        assertThat(c.numeroSeguimiento()).startsWith("RS-");
        assertThat(c.fechaProgramada()).isEqualTo(LocalDate.now().plusDays(2));
    }

    @Test
    @DisplayName("Los constructores por defecto crean su propio cliente de API")
    void constructoresPorDefecto() {
        assertThat(new TransAgroAdapter().solicitarDespacho(solicitud).numeroSeguimiento()).startsWith("TA-");
        assertThat(new RutaSurAdapter().solicitarDespacho(solicitud).numeroSeguimiento()).startsWith("RS-");
    }
}
