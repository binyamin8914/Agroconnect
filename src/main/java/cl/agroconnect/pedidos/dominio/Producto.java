package cl.agroconnect.pedidos.dominio;

import java.math.BigDecimal;

public record Producto(String codigo, String nombre, BigDecimal precio, double pesoKg) {
}
