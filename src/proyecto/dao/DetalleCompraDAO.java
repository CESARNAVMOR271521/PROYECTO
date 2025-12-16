package proyecto.dao;

import proyecto.Conexion;
import proyecto.modelo.DetalleCompra;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleCompraDAO {

    public boolean insertar(DetalleCompra detalle) {
        String sql = "INSERT INTO DetalleCompra (id_compra, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, detalle.getIdCompra());
            pstmt.setInt(2, detalle.getIdProducto());
            pstmt.setInt(3, detalle.getCantidad());
            pstmt.setDouble(4, detalle.getPrecioUnitario());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al insertar detalle compra: " + e.getMessage());
            return false;
        }
    }

    public List<DetalleCompra> listarPorCompra(int idCompra) {
        List<DetalleCompra> lista = new ArrayList<>();
        String sql = "SELECT * FROM DetalleCompra WHERE id_compra = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCompra);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DetalleCompra d = new DetalleCompra();
                    d.setIdDetalle(rs.getInt("id_detalle"));
                    d.setIdCompra(rs.getInt("id_compra"));
                    d.setIdProducto(rs.getInt("id_producto"));
                    d.setCantidad(rs.getInt("cantidad"));
                    d.setPrecioUnitario(rs.getDouble("precio_unitario"));
                    lista.add(d);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar detalles por compra: " + e.getMessage());
        }
        return lista;
    }
}
