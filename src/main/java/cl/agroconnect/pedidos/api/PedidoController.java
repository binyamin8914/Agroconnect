package cl.agroconnect.pedidos.api;

import cl.agroconnect.pedidos.aplicacion.ComandoRegistrarPedido;
import cl.agroconnect.pedidos.aplicacion.RegistrarPedidoService;
import cl.agroconnect.pedidos.dominio.Pedido;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final RegistrarPedidoService servicio;

    public PedidoController(RegistrarPedidoService servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> registrar(@Valid @RequestBody RegistrarPedidoRequest request) {
        ComandoRegistrarPedido comando = new ComandoRegistrarPedido(request.idCliente(),
                request.items().stream()
                        .map(i -> new ComandoRegistrarPedido.Item(i.codigoProducto(), i.cantidad()))
                        .toList(),
                request.modalidadDespacho(), request.transportista(), request.direccionEntrega());
        Pedido pedido = servicio.registrar(comando);
        return ResponseEntity.created(URI.create("/api/pedidos/" + pedido.getId()))
                .body(PedidoResponse.desde(pedido));
    }

    @GetMapping("/{id}")
    public PedidoResponse consultar(@PathVariable Long id) {
        return PedidoResponse.desde(servicio.consultar(id));
    }
}
