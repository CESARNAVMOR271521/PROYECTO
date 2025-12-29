package proyecto.prueba;

import proyecto.DatabaseHelper;
import proyecto.util.DataSeeder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestSeeder {
    public static void main(String[] args) {
        // Initialize DB which should trigger seeding
        DatabaseHelper.initDB();

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Verificando conteos...");
            printCount(stmt, "Proveedor");
            printCount(stmt, "Barbero");
            printCount(stmt, "Servicio");
            printCount(stmt, "Cliente");
            printCount(stmt, "Producto");
            printCount(stmt, "Inventario");
            printCount(stmt, "Usuario");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printCount(Statement stmt, String table) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table);
        if (rs.next()) {
            System.out.println(table + ": " + rs.getInt(1));
        }
    }
}
