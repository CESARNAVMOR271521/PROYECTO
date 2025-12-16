package proyecto.dao;

import proyecto.Conexion;
import proyecto.modelo.HorarioBarbero;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HorarioBarberoDAO {

    public boolean insertar(HorarioBarbero horario) {
        String sql = "INSERT INTO horario_barbero (id_barbero, dia_semana, hora_inicio, hora_fin) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, horario.getIdBarbero());
            pstmt.setString(2, horario.getDiaSemana());
            pstmt.setString(3, horario.getHoraInicio());
            pstmt.setString(4, horario.getHoraFin());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al insertar horario: " + e.getMessage());
            return false;
        }
    }

    public List<HorarioBarbero> listarPorBarbero(int idBarbero) {
        List<HorarioBarbero> lista = new ArrayList<>();
        String sql = "SELECT * FROM horario_barbero WHERE id_barbero = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idBarbero);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    HorarioBarbero h = new HorarioBarbero();
                    h.setIdHorario(rs.getInt("id_horario"));
                    h.setIdBarbero(rs.getInt("id_barbero"));
                    h.setDiaSemana(rs.getString("dia_semana"));
                    h.setHoraInicio(rs.getString("hora_inicio"));
                    h.setHoraFin(rs.getString("hora_fin"));
                    lista.add(h);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar horarios: " + e.getMessage());
        }
        return lista;
    }
}
