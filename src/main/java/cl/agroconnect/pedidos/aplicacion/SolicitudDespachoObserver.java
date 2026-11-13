package cl.agroconnect.pedidos.aplicacion;

import cl.agroconnect.pedidos.dominio.Pedido;
import cl.agroconnect.pedidos.dominio.evento.PedidoObserver;
import cl.agroconnect.pedidos.dominio.evento.PedidoRegistradoEvento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Observador concreto: al registrarse un pedido, solicita el despacho al transportista elegido. */
@Component
@Order(1)
public class SolicitudDespachoObserver implements PedidoObserver {

    private static final Logger log = LoggerFactory.getLogger(SolicitudDespachoObserver.class);

    private final RegistroTransportistas transportistas;

    public SolicitudDespachoObserver(RegistroTransportistas transportistas) {
        this.transportistas = transportistas;
    }

    @Override
    public void alRegistrarPedido(PedidoRegistradoEvento evento) {
        Pedido pedido = evento.pedido();
        if (RegistrarPedidoService.RETIRO_EN_BODEGA.equals(pedido.getModalidadDespacho())) {
            pedido.marcarListoParaRetiro();
            return;
        }
        ConfirmacionDespacho confirmacion = transportistas.obtener(pedido.getTransportista())
                .solicitarDespacho(new SolicitudDespacho(pedido.getId(), pedido.getDireccionEntrega(),
                        pedido.pesoTotalKg()));
        pedido.marcarDespachoSolicitado(confirmacion.numeroSeguimiento());
        log.info("Despacho solicitado pedido={} transportista={} seguimiento={}",
                pedido.getId(), confirmacion.transportista(), confirmacion.numeroSeguimiento());
    }
}
