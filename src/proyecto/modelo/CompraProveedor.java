package proyecto.modelo;

public class CompraProveedor {
    private int idCompra;
    private int idProveedor;
    private String fecha;
    private double total;

    public CompraProveedor() {}

    public CompraProveedor(int idCompra, int idProveedor, String fecha, double total) {
        this.idCompra = idCompra;
        this.idProveedor = idProveedor;
        this.fecha = fecha;
        this.total = total;
    }

    public int getIdCompra() { return idCompra; }
    public void setIdCompra(int idCompra) { this.idCompra = idCompra; }

    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
