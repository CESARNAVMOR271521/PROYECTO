package proyecto.dao;

import proyecto.Conexion;
import proyecto.modelo.Barbero;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BarberoDAO {

    public boolean insertar(Barbero barbero) {
        String sql = "INSERT INTO barbero (nombre, especialidades, activo) VALUES (?, ?, ?)";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, barbero.getNombre());
            pstmt.setString(2, barbero.getEspecialidades());
            pstmt.setInt(3, barbero.getActivo());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al insertar barbero: " + e.getMessage());
            return false;
        }
    }

    public List<Barbero> listar() {
        List<Barbero> lista = new ArrayList<>();
        String sql = "SELECT * FROM barbero";
        try (Connection conn = Conexion.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Barbero b = new Barbero();
                b.setIdBarbero(rs.getInt("id_barbero"));
                b.setNombre(rs.getString("nombre"));
                b.setEspecialidades(rs.getString("especialidades"));
                b.setActivo(rs.getInt("activo"));
                lista.add(b);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar barberos: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Barbero barbero) {
        String sql = "UPDATE barbero SET nombre = ?, especialidades = ?, activo = ? WHERE id_barbero = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, barbero.getNombre());
            pstmt.setString(2, barbero.getEspecialidades());
            pstmt.setInt(3, barbero.getActivo());
            pstmt.setInt(4, barbero.getIdBarbero());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar barbero: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idBarbero) {
        String sql = "DELETE FROM barbero WHERE id_barbero = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idBarbero);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar barbero: " + e.getMessage());
            return false;
        }
    }
}
