package co.edu.unab.stagepass.domain.model;

import java.time.LocalDateTime;

public class Evento {

    public enum Tipo { CONCIERTO, TEATRO, FESTIVAL }

    public enum Estado { PROGRAMADO, AGOTADO, CANCELADO }

    private final String id;
    private final String nombre;
    private final Tipo tipo;
    private final String recintoId;
    private final LocalDateTime fechaHora;
    private final Estado estado;

    public Evento(String id, String nombre, Tipo tipo, String recintoId, LocalDateTime fechaHora, Estado estado) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.recintoId = recintoId;
        this.fechaHora = fechaHora;
        this.estado = estado;
    }

    public boolean estaEnVenta() {
        return estado == Estado.PROGRAMADO;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public Tipo getTipo() { return tipo; }
    public String getRecintoId() { return recintoId; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public Estado getEstado() { return estado; }
}
