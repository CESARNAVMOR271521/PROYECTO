package proyecto.dao;

import proyecto.Conexion;
import proyecto.modelo.Inventario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioDAO {

    // 'insertar' in this context (after migration) effectively means setting initial stock params for a product
    // So we UPDATE the existing product record.
    public boolean insertar(Inventario inventario) {
        String sql = "UPDATE Producto SET cantidad_actual = ?, minimo = ? WHERE id_producto = ?";
        try (Connection conn = Conexion.conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, inventario.getStock());
            pstmt.setInt(2, inventario.getStockMinimo());
            pstmt.setInt(3, inventario.getIdProducto());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error al configurar inventario (UPDATE Producto): " + e.getMessage());
            return false;
        }
    }

    public List<Inventario> listar() {
        List<Inventario> lista = new ArrayList<>();
        // Select only products that "have inventory" (though all do now, maybe filter by some logic if needed)
        String sql = "SELECT id_producto, cantidad_actual, minimo FROM Producto";
        try (Connection conn = Conexion.conectar();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Inventario i = new Inventario();
                // We don't have id_inventario anymore, so we can set it to 0 or id_producto
                i.setIdInventario(rs.getInt("id_producto")); 
                i.setIdProducto(rs.getInt("id_producto"));
                i.setStock(rs.getInt("cantidad_actual"));
                i.setStockMinimo(rs.getInt("minimo"));
                lista.add(i);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar inventario (Producto): " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizarStock(int idProducto, int cantidad) {
        String sql = "UPDATE Producto SET cantidad_actual = ? WHERE id_producto = ?";
        try (Connection conn = Conexion.conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cantidad);
            pstmt.setInt(2, idProducto);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar stock (Producto): " + e.getMessage());
            return false;
        }
    }

    public boolean incrementarStock(int idProducto, int cantidad) {
        String sql = "UPDATE Producto SET cantidad_actual = cantidad_actual + ? WHERE id_producto = ?";
        try (Connection conn = Conexion.conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cantidad);
            pstmt.setInt(2, idProducto);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al incrementar stock (Producto): " + e.getMessage());
            return false;
        }
    }

    public Inventario obtenerPorProducto(int idProducto) {
        String sql = "SELECT id_producto, cantidad_actual, minimo FROM Producto WHERE id_producto = ?";
        try (Connection conn = Conexion.conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProducto);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Inventario i = new Inventario();
                    i.setIdInventario(rs.getInt("id_producto"));
                    i.setIdProducto(rs.getInt("id_producto"));
                    i.setStock(rs.getInt("cantidad_actual"));
                    i.setStockMinimo(rs.getInt("minimo"));
                    return i;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener inventario por producto: " + e.getMessage());
        }
        return null;
    }
}
