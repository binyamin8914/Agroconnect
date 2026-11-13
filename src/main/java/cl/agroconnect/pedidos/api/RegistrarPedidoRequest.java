package cl.agroconnect.pedidos.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record RegistrarPedidoRequest(
        @NotBlank String idCliente,
        @NotEmpty @Valid List<ItemRequest> items,
        @NotBlank String modalidadDespacho,
        String transportista,
        @NotBlank String direccionEntrega) {

    public record ItemRequest(@NotBlank String codigoProducto, @Min(1) int cantidad) {
    }
}
