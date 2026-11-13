package cl.agroconnect.pedidos.dominio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Pedido {

    private final Long id;
    private final String idCliente;
    private final List<LineaPedido> lineas;
    private final String modalidadDespacho;
    private final String transportista;
    private final String direccionEntrega;
    private final BigDecimal tarifaDespacho;
    private final LocalDateTime fecha;
    private EstadoPedido estado;
    private String numeroSeguimiento;

    public Pedido(Long id, String idCliente, List<LineaPedido> lineas, String modalidadDespacho,
                  String transportista, String direccionEntrega, BigDecimal tarifaDespacho) {
        if (lineas == null || lineas.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos un producto");
        }
        this.id = id;
        this.idCliente = idCliente;
        this.lineas = List.copyOf(lineas);
        this.modalidadDespacho = modalidadDespacho;
        this.transportista = transportista;
        this.direccionEntrega = direccionEntrega;
        this.tarifaDespacho = tarifaDespacho;
        this.fecha = LocalDateTime.now();
        this.estado = EstadoPedido.REGISTRADO;
    }

    public BigDecimal subtotalProductos() {
        return lineas.stream().map(LineaPedido::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal total() {
        return subtotalProductos().add(tarifaDespacho);
    }

    public double pesoTotalKg() {
        return lineas.stream().mapToDouble(LineaPedido::pesoKg).sum();
    }

    public void marcarDespachoSolicitado(String numeroSeguimiento) {
        this.numeroSeguimiento = numeroSeguimiento;
        this.estado = EstadoPedido.DESPACHO_SOLICITADO;
    }

    public void marcarListoParaRetiro() {
        this.estado = EstadoPedido.LISTO_PARA_RETIRO;
    }

    public Long getId() { return id; }
    public String getIdCliente() { return idCliente; }
    public List<LineaPedido> getLineas() { return lineas; }
    public String getModalidadDespacho() { return modalidadDespacho; }
    public String getTransportista() { return transportista; }
    public String getDireccionEntrega() { return direccionEntrega; }
    public BigDecimal getTarifaDespacho() { return tarifaDespacho; }
    public LocalDateTime getFecha() { return fecha; }
    public EstadoPedido getEstado() { return estado; }
    public String getNumeroSeguimiento() { return numeroSeguimiento; }
}
