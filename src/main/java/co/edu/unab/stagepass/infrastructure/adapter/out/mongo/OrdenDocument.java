package co.edu.unab.stagepass.infrastructure.adapter.out.mongo;

import co.edu.unab.stagepass.domain.model.Asiento;
import co.edu.unab.stagepass.domain.model.Cliente;
import co.edu.unab.stagepass.domain.model.MetodoPago;
import co.edu.unab.stagepass.domain.model.Orden;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Modelo de Mongo. Las anotaciones quedan aca y no en la clase Orden del dominio.
@Document("ordenes")
public class OrdenDocument {

    @Id
    public String id;
    public String eventoId;
    public Cliente cliente;
    public List<Asiento> asientos;
    public BigDecimal subtotal;
    public BigDecimal tarifaServicio;
    public BigDecimal descuento;
    public BigDecimal total;
    public LocalDateTime fechaCreacion;
    public LocalDateTime expiraEn;
    public String metodoPago;
    public String estado;
    public String referenciaPago;

    public static OrdenDocument desdeDominio(Orden o) {
        OrdenDocument d = new OrdenDocument();
        d.id = o.getId();
        d.eventoId = o.getEventoId();
        d.cliente = o.getCliente();
        d.asientos = o.getAsientos();
        d.subtotal = o.getSubtotal();
        d.tarifaServicio = o.getTarifaServicio();
        d.descuento = o.getDescuento();
        d.total = o.getTotal();
        d.fechaCreacion = o.getFechaCreacion();
        d.expiraEn = o.getExpiraEn();
        d.metodoPago = o.getMetodoPago().name();
        d.estado = o.getEstado().name();
        d.referenciaPago = o.getReferenciaPago();
        return d;
    }

    public Orden aDominio() {
        return new Orden(id, eventoId, cliente, asientos, subtotal, tarifaServicio, descuento, total,
                fechaCreacion, expiraEn, MetodoPago.valueOf(metodoPago), Orden.Estado.valueOf(estado), referenciaPago);
    }
}
