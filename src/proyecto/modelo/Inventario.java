package proyecto.modelo;

public class Inventario {
    private int idInventario;
    private int idProducto;
    private int stock;
    private int stockMinimo;

    public Inventario() {}

    public Inventario(int idInventario, int idProducto, int stock, int stockMinimo) {
        this.idInventario = idInventario;
        this.idProducto = idProducto;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
    }

    public int getIdInventario() { return idInventario; }
    public void setIdInventario(int idInventario) { this.idInventario = idInventario; }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }
}
