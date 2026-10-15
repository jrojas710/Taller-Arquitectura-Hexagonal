package co.edu.unab.stagepass.domain.model;

import java.math.BigDecimal;

public class Asiento {

    public enum Estado { DISPONIBLE, BLOQUEADO, VENDIDO }

    private final String id;
    private final String recintoId;
    private final String codigo;          // ej. VIP-A12, General-01
    private final BigDecimal precioBase;
    private Estado estado;

    public Asiento(String id, String recintoId, String codigo, BigDecimal precioBase, Estado estado) {
        this.id = id;
        this.recintoId = recintoId;
        this.codigo = codigo;
        this.precioBase = precioBase;
        this.estado = estado;
    }

    public String getId() { return id; }
    public String getRecintoId() { return recintoId; }
    public String getCodigo() { return codigo; }
    public BigDecimal getPrecioBase() { return precioBase; }
    public Estado getEstado() { return estado; }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }
}
