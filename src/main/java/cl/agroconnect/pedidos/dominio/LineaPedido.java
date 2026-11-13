package cl.agroconnect.pedidos.dominio;

import java.math.BigDecimal;

public record LineaPedido(Producto producto, int cantidad) {

    public LineaPedido {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
    }

    public BigDecimal subtotal() {
        return producto.precio().multiply(BigDecimal.valueOf(cantidad));
    }

    public double pesoKg() {
        return producto.pesoKg() * cantidad;
    }
}
