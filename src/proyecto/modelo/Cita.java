package proyecto.modelo;

public class Cita {
    private int idCita;
    private String fecha;
    private String hora;
    private int idCliente;
    private int idBarbero;
    private String estado;

    public Cita() {}

    public Cita(int idCita, String fecha, String hora, int idCliente, int idBarbero, String estado) {
        this.idCita = idCita;
        this.fecha = fecha;
        this.hora = hora;
        this.idCliente = idCliente;
        this.idBarbero = idBarbero;
        this.estado = estado;
    }

    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdBarbero() { return idBarbero; }
    public void setIdBarbero(int idBarbero) { this.idBarbero = idBarbero; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
