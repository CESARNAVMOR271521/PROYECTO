package proyecto;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("Testing connection using DatabaseHelper...");
        Connection conn1 = DatabaseHelper.connect();
        if (conn1 != null) {
            System.out.println("DatabaseHelper: Connection established successfully!");
            try {
                conn1.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("DatabaseHelper: Failed to connect.");
        }

        System.out.println("\nTesting connection using Conexion...");
        Connection conn2 = Conexion.conectar();
        if (conn2 != null) {
            System.out.println("Conexion: Connection established successfully!");
            try {
                conn2.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Conexion: Failed to connect.");
        }
    }
}
