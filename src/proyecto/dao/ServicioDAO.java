package proyecto.dao;

import proyecto.Conexion;
import proyecto.modelo.Servicio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAO {

    public boolean insertar(Servicio servicio) {
        String sql = "INSERT INTO servicio (nombre, descripcion, precio, duracion_minutos) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, servicio.getNombre());
            pstmt.setString(2, servicio.getDescripcion());
            pstmt.setDouble(3, servicio.getPrecio());
            pstmt.setInt(4, servicio.getDuracionMinutos());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al insertar servicio: " + e.getMessage());
            return false;
        }
    }

    public List<Servicio> listar() {
        List<Servicio> lista = new ArrayList<>();
        String sql = "SELECT * FROM servicio";
        try (Connection conn = Conexion.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Servicio s = new Servicio();
                s.setIdServicio(rs.getInt("id_servicio"));
                s.setNombre(rs.getString("nombre"));
                s.setDescripcion(rs.getString("descripcion"));
                s.setPrecio(rs.getDouble("precio"));
                s.setDuracionMinutos(rs.getInt("duracion_minutos"));
                lista.add(s);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar servicios: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Servicio servicio) {
        String sql = "UPDATE servicio SET nombre = ?, descripcion = ?, precio = ?, duracion_minutos = ? WHERE id_servicio = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, servicio.getNombre());
            pstmt.setString(2, servicio.getDescripcion());
            pstmt.setDouble(3, servicio.getPrecio());
            pstmt.setInt(4, servicio.getDuracionMinutos());
            pstmt.setInt(5, servicio.getIdServicio());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar servicio: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idServicio) {
        String sql = "DELETE FROM servicio WHERE id_servicio = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idServicio);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar servicio: " + e.getMessage());
            return false;
        }
    }
}
