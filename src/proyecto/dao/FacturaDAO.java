package proyecto.dao;

import proyecto.Conexion;
import proyecto.modelo.Factura;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacturaDAO {

    public boolean insertar(Factura factura) {
        String sql = "INSERT INTO factura (id_venta, total) VALUES (?, ?)";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, factura.getIdVenta());
            pstmt.setDouble(2, factura.getTotal());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al insertar factura: " + e.getMessage());
            return false;
        }
    }

    public List<Factura> listar() {
        List<Factura> lista = new ArrayList<>();
        String sql = "SELECT * FROM factura";
        try (Connection conn = Conexion.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Factura f = new Factura();
                f.setIdFactura(rs.getInt("id_factura"));
                f.setIdVenta(rs.getInt("id_venta"));
                f.setFechaEmision(rs.getString("fecha_emision"));
                f.setTotal(rs.getDouble("total"));
                lista.add(f);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar facturas: " + e.getMessage());
        }
        return lista;
    }

    public List<java.util.Map<String, Object>> listarConCliente() {
        List<java.util.Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT f.id_factura, f.id_venta, f.fecha_emision, f.total, " +
                     "COALESCE(c.nombre, 'Cliente Casual') as cliente_nombre " +
                     "FROM Factura f " +
                     "JOIN Venta v ON f.id_venta = v.id_venta " +
                     "LEFT JOIN Cliente c ON v.id_cliente = c.id_cliente";
                     
        try (Connection conn = Conexion.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("id_factura", rs.getInt("id_factura"));
                map.put("id_venta", rs.getInt("id_venta"));
                map.put("fecha", rs.getString("fecha_emision"));
                map.put("total", rs.getDouble("total"));
                map.put("cliente", rs.getString("cliente_nombre"));
                lista.add(map);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar facturas con cliente: " + e.getMessage());
        }
        return lista;
    }
}
