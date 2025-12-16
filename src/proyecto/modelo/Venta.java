package proyecto.modelo;

public class Venta {
    private int idVenta;
    private String fecha;
    private int idCliente;
    private double total;

    public Venta() {}

    public Venta(int idVenta, String fecha, int idCliente, double total) {
        this.idVenta = idVenta;
        this.fecha = fecha;
        this.idCliente = idCliente;
        this.total = total;
    }

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
