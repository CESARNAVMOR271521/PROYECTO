package proyecto.dao;

import proyecto.Conexion;
import proyecto.modelo.UsuarioSistema;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioSistemaDAO {

    public boolean insertar(UsuarioSistema usuario) {
        String sql = "INSERT INTO Usuario (nombre, usuario, password, rol) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexion.conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getUsuario());
            pstmt.setString(3, usuario.getPassword());
            pstmt.setString(4, usuario.getRol());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    public UsuarioSistema login(String usuario, String password) {
        String sql = "SELECT * FROM Usuario WHERE usuario = ? AND password = ?";
        try (Connection conn = Conexion.conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    UsuarioSistema u = new UsuarioSistema();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setNombre(rs.getString("nombre"));
                    u.setUsuario(rs.getString("usuario"));
                    u.setPassword(rs.getString("password"));
                    u.setRol(rs.getString("rol"));
                    return u;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en login: " + e.getMessage());
        }
        return null;
    }
}
