package proyecto.dao;

import proyecto.Conexion;
import proyecto.modelo.CompraProveedor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompraProveedorDAO {

    public int insertar(CompraProveedor compra) {
        String sql = "INSERT INTO compra_proveedor (id_proveedor, total, fecha) VALUES (?, ?, ?)";
        try (Connection conn = Conexion.conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, compra.getIdProveedor());
            pstmt.setDouble(2, compra.getTotal());
            pstmt.setString(3, compra.getFecha()); // Ensure date is passed
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            return -1;

        } catch (SQLException e) {
            System.out.println("Error al insertar compra: " + e.getMessage());
            return -1;
        }
    }

    public List<CompraProveedor> listar() {
        List<CompraProveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM compra_proveedor";

        try (Connection conn = Conexion.conectar();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                CompraProveedor c = new CompraProveedor();
                c.setIdCompra(rs.getInt("id_compra"));
                c.setIdProveedor(rs.getInt("id_proveedor"));
                c.setFecha(rs.getString("fecha"));
                c.setTotal(rs.getDouble("total"));
                lista.add(c);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar compras: " + e.getMessage());
        }

        return lista; // ✅ FALTABA
    } // ✅ FALTABA ESTA LLAVE

    public List<CompraProveedor> listarPorProveedor(int idProveedor) {
        List<CompraProveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM compra_proveedor WHERE id_proveedor = ?";

        try (Connection conn = Conexion.conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProveedor);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CompraProveedor c = new CompraProveedor();
                    c.setIdCompra(rs.getInt("id_compra"));
                    c.setIdProveedor(rs.getInt("id_proveedor"));
                    c.setFecha(rs.getString("fecha"));
                    c.setTotal(rs.getDouble("total"));
                    lista.add(c);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar compras por proveedor: " + e.getMessage());
        }

        return lista;
    }
}
