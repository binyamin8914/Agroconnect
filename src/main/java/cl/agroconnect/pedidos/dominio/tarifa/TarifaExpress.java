package cl.agroconnect.pedidos.dominio.tarifa;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Despacho express (24 h): $6.000 base + $300 por kg. */
public class TarifaExpress implements TarifaDespachoStrategy {

    @Override
    public String modalidad() {
        return "EXPRESS";
    }

    @Override
    public BigDecimal calcular(double pesoKg) {
        return BigDecimal.valueOf(6000 + 300 * pesoKg).setScale(0, RoundingMode.HALF_UP);
    }
}
