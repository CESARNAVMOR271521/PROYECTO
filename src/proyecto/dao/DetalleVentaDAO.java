package proyecto.dao;

import proyecto.Conexion;
import proyecto.modelo.DetalleVenta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleVentaDAO {

    public boolean insertar(DetalleVenta detalle) {
        String sql = "INSERT INTO detalle_venta (id_venta, tipo_item, id_item, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, detalle.getIdVenta());
            pstmt.setString(2, detalle.getTipoItem());
            pstmt.setInt(3, detalle.getIdItem());
            pstmt.setInt(4, detalle.getCantidad());
            pstmt.setDouble(5, detalle.getPrecioUnitario());
            pstmt.setDouble(6, detalle.getSubtotal());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al insertar detalle de venta: " + e.getMessage());
            return false;
        }
    }

    public List<DetalleVenta> listarPorVenta(int idVenta) {
        List<DetalleVenta> lista = new ArrayList<>();
        String sql = "SELECT * FROM detalle_venta WHERE id_venta = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idVenta);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DetalleVenta d = new DetalleVenta();
                    d.setIdDetalle(rs.getInt("id_detalle"));
                    d.setIdVenta(rs.getInt("id_venta"));
                    d.setTipoItem(rs.getString("tipo_item"));
                    d.setIdItem(rs.getInt("id_item"));
                    d.setCantidad(rs.getInt("cantidad"));
                    d.setPrecioUnitario(rs.getDouble("precio_unitario"));
                    d.setSubtotal(rs.getDouble("subtotal"));
                    lista.add(d);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar detalles de venta: " + e.getMessage());
        }
        return lista;
    }
}
