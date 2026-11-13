package cl.agroconnect.pedidos.dominio.tarifa;

import cl.agroconnect.pedidos.dominio.NegocioException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Contexto del patrón Strategy: elige la estrategia según la modalidad pedida. */
public class CalculadoraTarifa {

    private final Map<String, TarifaDespachoStrategy> estrategias;

    public CalculadoraTarifa(List<TarifaDespachoStrategy> estrategias) {
        this.estrategias = estrategias.stream()
                .collect(Collectors.toMap(TarifaDespachoStrategy::modalidad, Function.identity()));
    }

    public BigDecimal calcular(String modalidad, double pesoKg) {
        TarifaDespachoStrategy estrategia = estrategias.get(modalidad);
        if (estrategia == null) {
            throw new NegocioException("Modalidad de despacho no soportada: " + modalidad);
        }
        return estrategia.calcular(pesoKg);
    }
}
