package proyecto.dao;

import proyecto.Conexion;
import proyecto.modelo.Cita;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CitaDAO {

    public boolean insertar(Cita cita) {
        String sql = "INSERT INTO cita (fecha, hora, id_cliente, id_barbero, estado) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cita.getFecha());
            pstmt.setString(2, cita.getHora());
            pstmt.setInt(3, cita.getIdCliente());
            pstmt.setInt(4, cita.getIdBarbero());
            pstmt.setString(5, cita.getEstado());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al insertar cita: " + e.getMessage());
            return false;
        }
    }

    public List<Cita> listar() {
        List<Cita> lista = new ArrayList<>();
        String sql = "SELECT * FROM cita";
        try (Connection conn = Conexion.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Cita c = new Cita();
                c.setIdCita(rs.getInt("id_cita"));
                c.setFecha(rs.getString("fecha"));
                c.setHora(rs.getString("hora"));
                c.setIdCliente(rs.getInt("id_cliente"));
                c.setIdBarbero(rs.getInt("id_barbero"));
                c.setEstado(rs.getString("estado"));
                lista.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar citas: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Cita cita) {
        String sql = "UPDATE cita SET fecha = ?, hora = ?, id_cliente = ?, id_barbero = ?, estado = ? WHERE id_cita = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cita.getFecha());
            pstmt.setString(2, cita.getHora());
            pstmt.setInt(3, cita.getIdCliente());
            pstmt.setInt(4, cita.getIdBarbero());
            pstmt.setString(5, cita.getEstado());
            pstmt.setInt(6, cita.getIdCita());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar cita: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idCita) {
        String sql = "DELETE FROM cita WHERE id_cita = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCita);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar cita: " + e.getMessage());
            return false;
        }
    }
}
