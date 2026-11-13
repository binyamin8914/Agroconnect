package cl.agroconnect.pedidos.aplicacion;

import cl.agroconnect.pedidos.dominio.NegocioException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Registro de adaptadores disponibles; Spring inyecta todas las implementaciones de TransportistaPort. */
@Component
public class RegistroTransportistas {

    private final Map<String, TransportistaPort> transportistas;

    public RegistroTransportistas(List<TransportistaPort> adaptadores) {
        this.transportistas = adaptadores.stream()
                .collect(Collectors.toMap(TransportistaPort::codigo, Function.identity()));
    }

    public boolean existe(String codigo) {
        return transportistas.containsKey(codigo);
    }

    public TransportistaPort obtener(String codigo) {
        TransportistaPort port = transportistas.get(codigo);
        if (port == null) {
            throw new NegocioException("Transportista no integrado: " + codigo);
        }
        return port;
    }
}
