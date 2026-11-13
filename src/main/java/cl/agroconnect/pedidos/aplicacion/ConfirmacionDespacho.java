package cl.agroconnect.pedidos.aplicacion;

import java.time.LocalDate;

public record ConfirmacionDespacho(String transportista, String numeroSeguimiento, LocalDate fechaProgramada) {
}
