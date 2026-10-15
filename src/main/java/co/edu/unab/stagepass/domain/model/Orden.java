package co.edu.unab.stagepass.domain.model;

import co.edu.unab.stagepass.domain.exception.ReglaNegocioException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Orden {

    public enum Estado { PENDIENTE_PAGO, APROBADA, RECHAZADA, EXPIRADA }

    public static final int MINUTOS_BLOQUEO = 10;

    private final String id;
    private final String eventoId;
    private final Cliente cliente;
    private final List<Asiento> asientos;
    private final BigDecimal subtotal;
    private final BigDecimal tarifaServicio;
    private final BigDecimal descuento;
    private final BigDecimal total;
    private final LocalDateTime fechaCreacion;
    private final LocalDateTime expiraEn;
    private final MetodoPago metodoPago;
    private Estado estado;
    private String referenciaPago;

    public Orden(String id, String eventoId, Cliente cliente, List<Asiento> asientos, BigDecimal subtotal,
                 BigDecimal tarifaServicio, BigDecimal descuento, BigDecimal total, LocalDateTime fechaCreacion,
                 LocalDateTime expiraEn, MetodoPago metodoPago, Estado estado, String referenciaPago) {
        this.id = id;
        this.eventoId = eventoId;
        this.cliente = cliente;
        this.asientos = asientos;
        this.subtotal = subtotal;
        this.tarifaServicio = tarifaServicio;
        this.descuento = descuento;
        this.total = total;
        this.fechaCreacion = fechaCreacion;
        this.expiraEn = expiraEn;
        this.metodoPago = metodoPago;
        this.estado = estado;
        this.referenciaPago = referenciaPago;
    }

    // Pendiente de pago y ya pasaron los 10 minutos
    public boolean estaVencida(LocalDateTime ahora) {
        return estado == Estado.PENDIENTE_PAGO && !ahora.isBefore(expiraEn);
    }

    // La orden "ocupa" sus asientos si ya se pago o si sigue dentro de los 10 minutos
    public boolean ocupaAsientos(LocalDateTime ahora) {
        return estado == Estado.APROBADA || (estado == Estado.PENDIENTE_PAGO && ahora.isBefore(expiraEn));
    }

    public boolean tieneAsiento(String asientoId) {
        return asientos.stream().anyMatch(a -> a.getId().equals(asientoId));
    }

    public void aprobar() {
        validarPendiente();
        estado = Estado.APROBADA;
        asientos.forEach(a -> a.setEstado(Asiento.Estado.VENDIDO));
    }

    public void rechazar() {
        validarPendiente();
        estado = Estado.RECHAZADA;
        asientos.forEach(a -> a.setEstado(Asiento.Estado.DISPONIBLE));
    }

    public void expirar() {
        validarPendiente();
        estado = Estado.EXPIRADA;
        asientos.forEach(a -> a.setEstado(Asiento.Estado.DISPONIBLE));
    }

    private void validarPendiente() {
        if (estado != Estado.PENDIENTE_PAGO) {
            throw new ReglaNegocioException("La orden " + id + " ya no está pendiente de pago (estado " + estado + ")");
        }
    }

    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public Cliente getCliente() { return cliente; }
    public List<Asiento> getAsientos() { return asientos; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getTarifaServicio() { return tarifaServicio; }
    public BigDecimal getDescuento() { return descuento; }
    public BigDecimal getTotal() { return total; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getExpiraEn() { return expiraEn; }
    public MetodoPago getMetodoPago() { return metodoPago; }
    public Estado getEstado() { return estado; }
    public String getReferenciaPago() { return referenciaPago; }

    public void setReferenciaPago(String referenciaPago) {
        this.referenciaPago = referenciaPago;
    }
}
