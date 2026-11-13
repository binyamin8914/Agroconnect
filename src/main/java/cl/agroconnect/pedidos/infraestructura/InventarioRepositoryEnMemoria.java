package cl.agroconnect.pedidos.infraestructura;

import cl.agroconnect.pedidos.aplicacion.InventarioRepository;
import cl.agroconnect.pedidos.dominio.Producto;
import cl.agroconnect.pedidos.dominio.StockInsuficienteException;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Inventario en memoria (en producción: servicio de Inventario + PostgreSQL). */
@Repository
public class InventarioRepositoryEnMemoria implements InventarioRepository {

    private final Map<String, Producto> productos = Map.of(
            "P-001", new Producto("P-001", "Fertilizante NPK 25 kg", new BigDecimal("25000"), 25.0),
            "P-002", new Producto("P-002", "Semilla de maíz 10 kg", new BigDecimal("48000"), 10.0),
            "P-003", new Producto("P-003", "Herbicida 5 L", new BigDecimal("32000"), 5.5));

    private final Map<String, Integer> stock = new ConcurrentHashMap<>(Map.of(
            "P-001", 100, "P-002", 40, "P-003", 3));

    @Override
    public Optional<Producto> buscarProducto(String codigo) {
        return Optional.ofNullable(codigo).map(productos::get);
    }

    @Override
    public int stockDisponible(String codigo) {
        return stock.getOrDefault(codigo, 0);
    }

    @Override
    public void reservar(String codigo, int cantidad) {
        stock.compute(codigo, (k, actual) -> {
            int disponible = actual == null ? 0 : actual;
            if (disponible < cantidad) {
                throw new StockInsuficienteException(codigo);
            }
            return disponible - cantidad;
        });
    }
}
