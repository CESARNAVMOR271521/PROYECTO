package proyecto.prueba;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import proyecto.DatabaseHelper;

public class TestInventory {
    public static void main(String[] args) {
        // Init DB to trigger seeder
        DatabaseHelper.initDB();
        
        System.out.println("\n--- Current Inventory ---");
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Producto")) {
            
            while (rs.next()) {
                System.out.printf("ID: %d | Name: %s | Cat: %s | Price: %.2f%n", 
                    rs.getInt("id_producto"), 
                    rs.getString("nombre"), 
                    rs.getString("categoria"),
                    rs.getDouble("precio_venta"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
