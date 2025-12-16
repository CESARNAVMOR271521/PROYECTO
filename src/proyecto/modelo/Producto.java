package proyecto.modelo;

public class Producto {
    private int idProducto;
    private String nombre;
    private String descripcion;
    private double precioVenta;
    private double precioCompra;
    private int idProveedor;

    public Producto() {}

    public Producto(int idProducto, String nombre, String descripcion, double precioVenta, double precioCompra, int idProveedor) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioVenta = precioVenta;
        this.precioCompra = precioCompra;
        this.idProveedor = idProveedor;
    }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }

    public double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(double precioCompra) { this.precioCompra = precioCompra; }

    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }

    // Compatibility method for legacy code calling getPrecio/setPrecio
    public double getPrecio() { return precioVenta; }
    public void setPrecio(double precio) { this.precioVenta = precio; }
}
