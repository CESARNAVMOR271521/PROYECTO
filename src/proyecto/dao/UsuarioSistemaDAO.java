package proyecto.dao;

import proyecto.DatabaseHelper;
import proyecto.modelo.UsuarioSistema;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioSistemaDAO {

    public boolean insertar(UsuarioSistema usuario) {
        // Corrected column names: nombre_usuario, password, rol
        // id_usuario is likely auto-increment/integer primary key, so we omit it from insert
        String sql = "INSERT INTO Usuario (nombre_usuario, password, rol) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getNombreUsuario());
            // Hash password antes de guardar
            String hashedPassword = proyecto.util.PasswordUtil.hashPassword(usuario.getPassword());
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, usuario.getRol());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    public UsuarioSistema login(String nombreUsuario, String password) {
        // Buscar por nombre_usuario
        String sql = "SELECT * FROM Usuario WHERE nombre_usuario = ?";
        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombreUsuario);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    // Verificar contraseña
                    if (proyecto.util.PasswordUtil.checkPassword(password, storedHash)) {
                        UsuarioSistema u = new UsuarioSistema();
                        u.setIdUsuario(rs.getInt("id_usuario"));
                        u.setNombreUsuario(rs.getString("nombre_usuario")); // Columna correcta
                        u.setPassword(storedHash); // Mantenemos el hash en el objeto
                        u.setRol(rs.getString("rol"));
                        return u;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en login: " + e.getMessage());
        }
        return null;
    }
}
