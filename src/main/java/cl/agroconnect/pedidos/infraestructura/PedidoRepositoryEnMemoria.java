package cl.agroconnect.pedidos.infraestructura;

import cl.agroconnect.pedidos.aplicacion.PedidoRepository;
import cl.agroconnect.pedidos.dominio.Pedido;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PedidoRepositoryEnMemoria implements PedidoRepository {

    private final Map<Long, Pedido> pedidos = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(0);

    @Override
    public Long siguienteId() {
        return secuencia.incrementAndGet();
    }

    @Override
    public Pedido guardar(Pedido pedido) {
        pedidos.put(pedido.getId(), pedido);
        return pedido;
    }

    @Override
    public Optional<Pedido> buscarPorId(Long id) {
        return Optional.ofNullable(pedidos.get(id));
    }
}
