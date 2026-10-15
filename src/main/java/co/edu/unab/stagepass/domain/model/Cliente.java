package co.edu.unab.stagepass.domain.model;

public class Cliente {

    private final String id;
    private final String nombre;
    private final String correo;
    private final String telefono;
    private final String nacionalidad;

    public Cliente(String id, String nombre, String correo, String telefono, String nacionalidad) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.nacionalidad = nacionalidad;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getTelefono() { return telefono; }
    public String getNacionalidad() { return nacionalidad; }
}
