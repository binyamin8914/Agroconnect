package cl.agroconnect.pedidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Servicio de Pedidos de AgroConnect.
 * Componente mínimo del Proyecto Final: implementa el caso de uso RF-04 "Registrar pedido".
 */
@SpringBootApplication
public class PedidosApplication {

    public static void main(String[] args) {
        SpringApplication.run(PedidosApplication.class, args);
    }
}
