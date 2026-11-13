package cl.agroconnect.pedidos.dominio.tarifa;

import java.math.BigDecimal;

/** El cliente retira en bodega: sin costo de despacho. */
public class TarifaRetiroEnBodega implements TarifaDespachoStrategy {

    @Override
    public String modalidad() {
        return "RETIRO_EN_BODEGA";
    }

    @Override
    public BigDecimal calcular(double pesoKg) {
        return BigDecimal.ZERO;
    }
}
