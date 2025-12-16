package proyecto.dao;

import proyecto.Conexion;
import proyecto.modelo.Pago;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO {

    public boolean insertar(Pago pago) {
        String sql = "INSERT INTO pago (id_venta, forma_pago, monto, estado) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, pago.getIdVenta());
            pstmt.setString(2, pago.getFormaPago());
            pstmt.setDouble(3, pago.getMonto());
            pstmt.setString(4, pago.getEstado());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al insertar pago: " + e.getMessage());
            return false;
        }
    }

    public List<Pago> listar() {
        List<Pago> lista = new ArrayList<>();
        String sql = "SELECT * FROM pago";
        try (Connection conn = Conexion.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Pago p = new Pago();
                p.setIdPago(rs.getInt("id_pago"));
                p.setIdVenta(rs.getInt("id_venta"));
                p.setFormaPago(rs.getString("forma_pago"));
                p.setMonto(rs.getDouble("monto"));
                p.setEstado(rs.getString("estado"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar pagos: " + e.getMessage());
        }
        return lista;
    }
}
