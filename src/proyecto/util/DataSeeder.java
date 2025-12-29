package proyecto.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import proyecto.DatabaseHelper;

public class DataSeeder {

    public static void seed() {
        try (Connection conn = DatabaseHelper.connect()) {
            if (conn == null) return;
            
            seedProveedores(conn);
            seedBarberos(conn); // Must be before Citas if we were seeding Citas
            seedServicios(conn);
            seedClientes(conn);
            seedUsuarios(conn);
            seedProductosAndInventario(conn); // Depends on Proveedores
            
            System.out.println("Semillas de datos insertadas correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void seedProveedores(Connection conn) throws SQLException {
        if (count(conn, "Proveedor") >= 4) return;
        
        String sql = "INSERT INTO Proveedor(nombre, telefono, correo) VALUES(?,?,?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            addBatch(pst, "Distribuidora BarberKing", "555-0101", "contacto@barberking.com");
            addBatch(pst, "Productos Capilares MX", "555-0102", "ventas@capilaresmx.com");
            addBatch(pst, "Herramientas de Corte Pro", "555-0103", "info@cortepro.com");
            addBatch(pst, "Importadora Cosmética", "555-0104", "pedidos@importcosmetica.com");
            pst.executeBatch();
        }
    }

    private static void seedBarberos(Connection conn) throws SQLException {
        if (count(conn, "Barbero") >= 4) return;
        
        String sql = "INSERT INTO Barbero(nombre, especialidades, activo) VALUES(?,?,?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            addBatch(pst, "Carlos Mendoza", "Corte Clásico, Barba", 1);
            addBatch(pst, "Luis Hernández", "Degradados, Diseños", 1);
            addBatch(pst, "Ana Torres", "Colorimetría, Corte Moderno", 1);
            addBatch(pst, "Miguel Ángel", "Afeitado Tradicional, Masajes", 1);
            pst.executeBatch();
        }
    }

    private static void seedServicios(Connection conn) throws SQLException {
        if (count(conn, "Servicio") >= 4) return;
        
        String sql = "INSERT INTO Servicio(nombre, descripcion, precio) VALUES(?,?,?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            addBatch(pst, "Corte de Cabello", "Corte completo con tijera o máquina", 150.00);
            addBatch(pst, "Afeitado de Barba", "Afeitado con navaja y toalla caliente", 120.00);
            addBatch(pst, "Corte + Barba", "Paquete completo de corte y afeitado", 250.00);
            addBatch(pst, "Limpieza Facial", "Exfoliación y mascarilla", 200.00);
            pst.executeBatch();
        }
    }

    private static void seedClientes(Connection conn) throws SQLException {
        if (count(conn, "Cliente") >= 4) return;
        
        String sql = "INSERT INTO Cliente(nombre, telefono, correo, historial) VALUES(?,?,?,?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            addBatch(pst, "Juan Pérez", "555-1111", "juan@mail.com", "Nuevo cliente");
            addBatch(pst, "Roberto Gómez", "555-2222", "roberto@mail.com", "Cliente frecuente");
            addBatch(pst, "María López", "555-3333", "maria@mail.com", "Prefiere citas por la tarde");
            addBatch(pst, "Pedro Sánchez", "555-4444", "pedro@mail.com", "Alergia a lociones fuertes");
            pst.executeBatch();
        }
    }

    private static void seedUsuarios(Connection conn) throws SQLException {
        if (count(conn, "Usuario") >= 4) return;
        
        // Note: Passwords should ideally be hashed, but schema suggests plain text for now or handled elsewhere.
        // Inserting simple passwords for demo.
        String sql = "INSERT INTO Usuario(nombre, usuario, password, rol) VALUES(?,?,?,?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            addBatch(pst, "Administrador", "admin", "admin123", "Administrador");
            addBatch(pst, "Recepcionista", "recepcion", "recepcion123", "Recepcionista");
            addBatch(pst, "Gerente", "gerente", "gerente123", "Gerente");
            addBatch(pst, "Carlos Barbero", "carlos", "barbero123", "Barbero");
            pst.executeBatch();
        }
    }

    private static void seedProductosAndInventario(Connection conn) throws SQLException {
        // Get Provider IDs
        int provId1 = getFirstId(conn, "Proveedor");
        
        // Safety Check: If no provider exists, create one to avoid FK violation
        if (provId1 <= 0) {
            try (PreparedStatement pstP = conn.prepareStatement("INSERT INTO Proveedor(nombre) VALUES(?)", Statement.RETURN_GENERATED_KEYS)) {
                pstP.setString(1, "Proveedor General");
                pstP.executeUpdate();
                ResultSet rs = pstP.getGeneratedKeys();
                if (rs.next()) provId1 = rs.getInt(1);
            }
        }

        String sqlCheck = "SELECT id_producto FROM Producto WHERE nombre = ?";
        String sqlProd = "INSERT INTO Producto(nombre, descripcion, categoria, precio_venta, precio_compra, id_proveedor) VALUES(?,?,?,?,?,?)";
        String sqlInv = "INSERT INTO Inventario(id_producto, cantidad_actual, minimo) VALUES(?,?,?)";
        
        try (PreparedStatement pstCheck = conn.prepareStatement(sqlCheck);
             PreparedStatement pstProd = conn.prepareStatement(sqlProd, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement pstInv = conn.prepareStatement(sqlInv)) {
            
            // List of products to seed
            Object[][] products = {
                // Name, Description, Category, Price Sale, Price Buy, Stock, Min
                {"Cera Modeladora", "Fijación fuerte mate", "Cabello", 180.00, 90.00, 50, 10},
                {"Gel Extra Fuerte", "Efecto húmedo duradero", "Cabello", 120.00, 60.00, 40, 5},
                {"Aceite para Barba", "Hidratación y brillo", "Barba", 200.00, 100.00, 30, 5},
                {"Shampoo Anticaspa", "Control total", "Cabello", 150.00, 75.00, 25, 8},
                {"Crema de Afeitar", "Espuma suave para afeitado", "Afeitado", 85.00, 40.00, 40, 5},
                {"Navajas de Afeitar", "Paquete de 10 navajas platinum", "Afeitado", 50.00, 20.00, 100, 20},
                {"Talco Barbería", "Talco fino para cuello", "Afeitado", 95.00, 45.00, 20, 3},
                {"Loción Aftershave", "Refrescante para después del afeitado", "Afeitado", 160.00, 80.00, 35, 5},
                {"Capa de Corte", "Capa profesional repelente al agua", "Herramientas", 250.00, 120.00, 10, 2},
                {"Peine de Carbono", "Resistente al calor y antiestático", "Herramientas", 60.00, 25.00, 50, 5},
                // New items added based on request
                {"Cool Care Desinfectante", "Spray 5 en 1 para máquinas", "Herramientas", 350.00, 220.00, 15, 3},
                {"Papel Cuello", "Rollo de papel elástico (Paquete)", "Otros", 120.00, 70.00, 60, 10},
                {"Mascarilla Negra", "Limpieza profunda de poros", "Otros", 45.00, 20.00, 50, 10},
                {"Minoxidil 5%", "Tratamiento para crecimiento de barba", "Barba", 450.00, 250.00, 20, 5},
                {"Cera en Polvo", "Volumen y textura mate", "Cabello", 220.00, 110.00, 25, 5},
                {"Cepillo Fade", "Cepillo suave para degradados", "Herramientas", 150.00, 75.00, 15, 3},
                {"Atomizador Premium", "Rociador de bruma fina", "Herramientas", 180.00, 90.00, 20, 4},
                {"Pomada Base Agua", "Brillo medio fijación media", "Cabello", 160.00, 80.00, 40, 8}
            };

            for (Object[] prod : products) {
                String name = (String) prod[0];
                pstCheck.setString(1, name);
                ResultSet rs = pstCheck.executeQuery();
                
                if (!rs.next()) { // Only insert if not exists
                    pstProd.setString(1, name);
                    pstProd.setString(2, (String) prod[1]);
                    pstProd.setString(3, (String) prod[2]);
                    pstProd.setDouble(4, (double) prod[3]);
                    pstProd.setDouble(5, (double) prod[4]);
                    pstProd.setInt(6, provId1);
                    pstProd.executeUpdate();
                    
                    int id = getGeneratedKey(pstProd);
                    if (id != -1) {
                        pstInv.setInt(1, id);
                        pstInv.setInt(2, (int) prod[5]);
                        pstInv.setInt(3, (int) prod[6]);
                        pstInv.executeUpdate();
                        System.out.println("Producto agregado: " + name);
                    }
                }
            }
        }
    }

    // Helper methods
    private static int count(Connection conn, String table) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }
    
    private static int getFirstId(Connection conn, String table) throws SQLException {
        String idColumn = "id_" + table.toLowerCase(); // Simple heuristic: Proveedor -> id_proveedor
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT " + idColumn + " FROM " + table + " LIMIT 1")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
             System.out.println("Warning: Could not get ID for " + table);
        }
        return -1; // Default fallback indicating failure
    }

    private static int getGeneratedKey(Statement stmt) throws SQLException {
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

    private static void addBatch(PreparedStatement pst, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            pst.setObject(i + 1, params[i]);
        }
        pst.addBatch();
    }
}
