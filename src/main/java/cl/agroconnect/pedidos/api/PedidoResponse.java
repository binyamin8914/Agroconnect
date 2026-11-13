package cl.agroconnect.pedidos.api;

import cl.agroconnect.pedidos.dominio.Pedido;

import java.math.BigDecimal;
import java.util.List;

public record PedidoResponse(Long id, String idCliente, String estado, String modalidadDespacho,
                             String transportista, String numeroSeguimiento, List<Linea> lineas,
                             BigDecimal subtotalProductos, BigDecimal tarifaDespacho, BigDecimal total) {

    public record Linea(String codigoProducto, String nombre, int cantidad, BigDecimal subtotal) {
    }

    public static PedidoResponse desde(Pedido p) {
        List<Linea> lineas = p.getLineas().stream()
                .map(l -> new Linea(l.producto().codigo(), l.producto().nombre(), l.cantidad(), l.subtotal()))
                .toList();
        return new PedidoResponse(p.getId(), p.getIdCliente(), p.getEstado().name(), p.getModalidadDespacho(),
                p.getTransportista(), p.getNumeroSeguimiento(), lineas, p.subtotalProductos(),
                p.getTarifaDespacho(), p.total());
    }
}
