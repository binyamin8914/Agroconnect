package cl.agroconnect.pedidos.aplicacion;

import cl.agroconnect.pedidos.dominio.Pedido;

import java.util.Optional;

public interface PedidoRepository {

    Long siguienteId();

    Pedido guardar(Pedido pedido);

    Optional<Pedido> buscarPorId(Long id);
}
