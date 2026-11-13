package cl.agroconnect.pedidos.aplicacion;

import cl.agroconnect.pedidos.dominio.Cliente;

import java.util.Optional;

public interface ClienteRepository {
    Optional<Cliente> buscarPorId(String idCliente);
}
