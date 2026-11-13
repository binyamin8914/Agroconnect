package cl.agroconnect.pedidos.dominio.tarifa;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Despacho estándar (48-72 h): $3.000 base + $150 por kg. */
public class TarifaEstandar implements TarifaDespachoStrategy {

    @Override
    public String modalidad() {
        return "ESTANDAR";
    }

    @Override
    public BigDecimal calcular(double pesoKg) {
        return BigDecimal.valueOf(3000 + 150 * pesoKg).setScale(0, RoundingMode.HALF_UP);
    }
}
