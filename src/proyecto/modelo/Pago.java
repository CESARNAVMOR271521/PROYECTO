package proyecto.modelo;

public class Pago {
    private int idPago;
    private int idVenta;
    private String formaPago;
    private double monto;
    private String estado;

    public Pago() {}

    public Pago(int idPago, int idVenta, String formaPago, double monto, String estado) {
        this.idPago = idPago;
        this.idVenta = idVenta;
        this.formaPago = formaPago;
        this.monto = monto;
        this.estado = estado;
    }

    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public String getFormaPago() { return formaPago; }
    public void setFormaPago(String formaPago) { this.formaPago = formaPago; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
