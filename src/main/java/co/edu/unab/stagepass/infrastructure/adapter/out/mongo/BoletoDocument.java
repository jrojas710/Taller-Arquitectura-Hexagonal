package co.edu.unab.stagepass.infrastructure.adapter.out.mongo;

import co.edu.unab.stagepass.domain.model.Boleto;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("boletos")
public class BoletoDocument {

    @Id
    public String id;
    public String ordenId;
    public String asientoId;
    public String asientoCodigo;
    public String codigoRedencion;
    public String firmaHash;
    public String qr;
    public LocalDateTime fechaEmision;

    public static BoletoDocument desdeDominio(Boleto b) {
        BoletoDocument d = new BoletoDocument();
        d.id = b.getId();
        d.ordenId = b.getOrdenId();
        d.asientoId = b.getAsientoId();
        d.asientoCodigo = b.getAsientoCodigo();
        d.codigoRedencion = b.getCodigoRedencion();
        d.firmaHash = b.getFirmaHash();
        d.qr = b.getQr();
        d.fechaEmision = b.getFechaEmision();
        return d;
    }

    public Boleto aDominio() {
        return new Boleto(id, ordenId, asientoId, asientoCodigo, codigoRedencion, firmaHash, qr, fechaEmision);
    }
}
