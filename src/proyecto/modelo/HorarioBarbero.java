package proyecto.modelo;

public class HorarioBarbero {
    private int idHorario;
    private int idBarbero;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;

    public HorarioBarbero() {}

    public HorarioBarbero(int idHorario, int idBarbero, String diaSemana, String horaInicio, String horaFin) {
        this.idHorario = idHorario;
        this.idBarbero = idBarbero;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public int getIdHorario() { return idHorario; }
    public void setIdHorario(int idHorario) { this.idHorario = idHorario; }

    public int getIdBarbero() { return idBarbero; }
    public void setIdBarbero(int idBarbero) { this.idBarbero = idBarbero; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }
}
