package cl.agroconnect.pedidos.aplicacion;

import cl.agroconnect.pedidos.dominio.Producto;

import java.util.Optional;

public interface InventarioRepository {

    Optional<Producto> buscarProducto(String codigo);

    int stockDisponible(String codigo);

    void reservar(String codigo, int cantidad);
}
