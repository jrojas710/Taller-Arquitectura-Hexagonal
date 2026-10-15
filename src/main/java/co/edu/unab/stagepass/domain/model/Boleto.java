package co.edu.unab.stagepass.domain.model;

import java.time.LocalDateTime;

public class Boleto {

    private final String id;
    private final String ordenId;
    private final String asientoId;
    private final String asientoCodigo;
    private final String codigoRedencion;
    private final String firmaHash;
    private final String qr;
    private final LocalDateTime fechaEmision;

    public Boleto(String id, String ordenId, String asientoId, String asientoCodigo, String codigoRedencion,
                  String firmaHash, String qr, LocalDateTime fechaEmision) {
        this.id = id;
        this.ordenId = ordenId;
        this.asientoId = asientoId;
        this.asientoCodigo = asientoCodigo;
        this.codigoRedencion = codigoRedencion;
        this.firmaHash = firmaHash;
        this.qr = qr;
        this.fechaEmision = fechaEmision;
    }

    // Datos inmutables de la orden que se usan para calcular la firma del boleto
    public static String datosParaFirma(Orden orden, Asiento asiento, String codigoRedencion) {
        return orden.getId() + "|" + orden.getEventoId() + "|" + orden.getCliente().getId() + "|"
                + asiento.getId() + "|" + codigoRedencion;
    }

    public String getId() { return id; }
    public String getOrdenId() { return ordenId; }
    public String getAsientoId() { return asientoId; }
    public String getAsientoCodigo() { return asientoCodigo; }
    public String getCodigoRedencion() { return codigoRedencion; }
    public String getFirmaHash() { return firmaHash; }
    public String getQr() { return qr; }
    public LocalDateTime getFechaEmision() { return fechaEmision; }
}
