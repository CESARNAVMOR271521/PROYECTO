package proyecto.modelo;

public class Factura {
    private int idFactura;
    private int idVenta;
    private String fechaEmision;
    private double total;

    public Factura() {}

    public Factura(int idFactura, int idVenta, String fechaEmision, double total) {
        this.idFactura = idFactura;
        this.idVenta = idVenta;
        this.fechaEmision = fechaEmision;
        this.total = total;
    }

    public int getIdFactura() { return idFactura; }
    public void setIdFactura(int idFactura) { this.idFactura = idFactura; }

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public String getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(String fechaEmision) { this.fechaEmision = fechaEmision; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
