package cl.agroconnect.pedidos.dominio.tarifa;

import java.math.BigDecimal;

/**
 * Patrón Strategy: cada modalidad de despacho calcula su tarifa con un algoritmo distinto.
 * Agregar una modalidad nueva es agregar una clase, sin tocar RegistrarPedidoService.
 */
public interface TarifaDespachoStrategy {

    String modalidad();

    BigDecimal calcular(double pesoKg);
}
