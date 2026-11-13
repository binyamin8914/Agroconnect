package cl.agroconnect.pedidos.aplicacion;

import java.util.List;

public record ComandoRegistrarPedido(String idCliente, List<Item> items, String modalidadDespacho,
                                     String transportista, String direccionEntrega) {

    public record Item(String codigoProducto, int cantidad) {
    }
}
