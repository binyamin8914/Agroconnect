package cl.agroconnect.pedidos.dominio;

import cl.agroconnect.pedidos.dominio.tarifa.CalculadoraTarifa;
import cl.agroconnect.pedidos.dominio.tarifa.TarifaEstandar;
import cl.agroconnect.pedidos.dominio.tarifa.TarifaExpress;
import cl.agroconnect.pedidos.dominio.tarifa.TarifaRetiroEnBodega;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Patrón Strategy: cálculo de tarifa de despacho")
class CalculadoraTarifaTest {

    private final CalculadoraTarifa calculadora = new CalculadoraTarifa(
            List.of(new TarifaEstandar(), new TarifaExpress(), new TarifaRetiroEnBodega()));

    @Test
    @DisplayName("ESTANDAR cobra 3.000 + 150 por kg")
    void tarifaEstandar() {
        assertThat(calculadora.calcular("ESTANDAR", 50)).isEqualByComparingTo(new BigDecimal("10500"));
    }

    @Test
    @DisplayName("EXPRESS cobra 6.000 + 300 por kg")
    void tarifaExpress() {
        assertThat(calculadora.calcular("EXPRESS", 10)).isEqualByComparingTo(new BigDecimal("9000"));
    }

    @Test
    @DisplayName("RETIRO_EN_BODEGA no tiene costo")
    void retiroEnBodega() {
        assertThat(calculadora.calcular("RETIRO_EN_BODEGA", 999)).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Una modalidad desconocida se rechaza")
    void modalidadDesconocida() {
        assertThatThrownBy(() -> calculadora.calcular("DRON", 1))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("DRON");
    }
}
