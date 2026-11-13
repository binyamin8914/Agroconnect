package cl.agroconnect.pedidos.infraestructura;

import cl.agroconnect.pedidos.dominio.evento.PedidoObserver;
import cl.agroconnect.pedidos.dominio.evento.PublicadorEventosPedido;
import cl.agroconnect.pedidos.dominio.tarifa.CalculadoraTarifa;
import cl.agroconnect.pedidos.dominio.tarifa.TarifaEstandar;
import cl.agroconnect.pedidos.dominio.tarifa.TarifaExpress;
import cl.agroconnect.pedidos.dominio.tarifa.TarifaRetiroEnBodega;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/** Ensambla los objetos del dominio (que no dependen de Spring) como beans. */
@Configuration
public class ConfiguracionDominio {

    @Bean
    public CalculadoraTarifa calculadoraTarifa() {
        return new CalculadoraTarifa(List.of(new TarifaEstandar(), new TarifaExpress(), new TarifaRetiroEnBodega()));
    }

    @Bean
    public PublicadorEventosPedido publicadorEventosPedido(List<PedidoObserver> observadores) {
        return new PublicadorEventosPedido(observadores);
    }
}
