package proyecto.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import proyecto.DatabaseHelper;

public class RegistroVozDAO {

    public void insertar(String texto) {
        String sql = "INSERT INTO RegistroVoz(texto) VALUES(?)";
        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, texto);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al guardar registro de voz: " + e.getMessage());
        }
    }

    public List<String[]> listar() {
        List<String[]> logs = new ArrayList<>();
        String sql = "SELECT id_log, texto, fecha FROM RegistroVoz ORDER BY id_log DESC";

        try (Connection conn = DatabaseHelper.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                logs.add(new String[] {
                        String.valueOf(rs.getInt("id_log")),
                        rs.getString("texto"),
                        rs.getString("fecha")
                });
            }
        } catch (SQLException e) {
            System.out.println("Error al listar registros de voz: " + e.getMessage());
        }
        return logs;
    }
}
