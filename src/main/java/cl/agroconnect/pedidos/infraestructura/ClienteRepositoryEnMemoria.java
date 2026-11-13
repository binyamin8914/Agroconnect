package cl.agroconnect.pedidos.infraestructura;

import cl.agroconnect.pedidos.aplicacion.ClienteRepository;
import cl.agroconnect.pedidos.dominio.Cliente;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

/** Repositorio en memoria (en producción: PostgreSQL vía servicio de Clientes). */
@Repository
public class ClienteRepositoryEnMemoria implements ClienteRepository {

    private final Map<String, Cliente> clientes = Map.of(
            "C-001", new Cliente("C-001", "Agrícola Los Robles", "compras@losrobles.cl", true),
            "C-002", new Cliente("C-002", "Viñedos del Maule", "contacto@vmaule.cl", true),
            "C-003", new Cliente("C-003", "Hortalizas Ñuble (inactivo)", "ventas@hnuble.cl", false));

    @Override
    public Optional<Cliente> buscarPorId(String idCliente) {
        return Optional.ofNullable(idCliente).map(clientes::get);
    }
}
