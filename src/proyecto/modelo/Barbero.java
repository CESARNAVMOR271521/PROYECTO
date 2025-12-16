package proyecto.modelo;

public class Barbero {
    private int idBarbero;
    private String nombre;
    private String especialidades;
    private int activo;

    public Barbero() {}

    public Barbero(int idBarbero, String nombre, String especialidades, int activo) {
        this.idBarbero = idBarbero;
        this.nombre = nombre;
        this.especialidades = especialidades;
        this.activo = activo;
    }

    public int getIdBarbero() { return idBarbero; }
    public void setIdBarbero(int idBarbero) { this.idBarbero = idBarbero; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEspecialidades() { return especialidades; }
    public void setEspecialidades(String especialidades) { this.especialidades = especialidades; }

    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }
}
