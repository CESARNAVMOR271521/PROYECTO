package proyecto.dao;

import proyecto.Conexion;
import proyecto.modelo.Inventario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioDAO {

    public boolean insertar(Inventario inventario) {
        String sql = "INSERT INTO inventario (id_producto, cantidad_actual, minimo) VALUES (?, ?, ?)";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, inventario.getIdProducto());
            pstmt.setInt(2, inventario.getStock());
            pstmt.setInt(3, inventario.getStockMinimo());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al insertar inventario: " + e.getMessage());
            return false;
        }
    }

    public List<Inventario> listar() {
        List<Inventario> lista = new ArrayList<>();
        String sql = "SELECT * FROM inventario";
        try (Connection conn = Conexion.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Inventario i = new Inventario();
                i.setIdInventario(rs.getInt("id_inventario"));
                i.setIdProducto(rs.getInt("id_producto"));
                i.setStock(rs.getInt("cantidad_actual"));
                i.setStockMinimo(rs.getInt("minimo"));
                lista.add(i);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar inventario: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizarStock(int idProducto, int cantidad) {
        String sql = "UPDATE inventario SET cantidad_actual = ? WHERE id_producto = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cantidad);
            pstmt.setInt(2, idProducto);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar stock: " + e.getMessage());
            return false;
        }
    }

    public Inventario obtenerPorProducto(int idProducto) {
        String sql = "SELECT * FROM inventario WHERE id_producto = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProducto);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Inventario i = new Inventario();
                    i.setIdInventario(rs.getInt("id_inventario"));
                    i.setIdProducto(rs.getInt("id_producto"));
                    i.setStock(rs.getInt("cantidad_actual"));
                    i.setStockMinimo(rs.getInt("minimo"));
                    return i;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener inventario por producto: " + e.getMessage());
        }
        return null; // O devolver un inventario con stock 0
    }
}
