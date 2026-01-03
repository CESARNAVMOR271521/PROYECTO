package proyecto.prueba;

import proyecto.ClientesPanel;
import proyecto.ProductosPanel;
import proyecto.ProveedoresPanel;
import proyecto.VentasPanel;
import proyecto.CitasPanel;
import proyecto.VoiceAware;
import javax.swing.SwingUtilities;

public class TestVoiceCommands {

    public static void main(String[] args) {
        System.out.println("=== INICIANDO TEST DE COMANDOS DE VOZ ===");
        
        try {
            // 1. Test ClientesPanel
            System.out.println("\n[TEST] ClientesPanel...");
            ClientesPanel clientes = new ClientesPanel();
            testCommand(clientes, "CLEAR", "");
            testCommand(clientes, "SET_FIELD", "NOMBRE TEST_VOZ");
            testCommand(clientes, "SELECT", "Juan"); // Should not crash
            System.out.println("[OK] ClientesPanel logic passed.");
            
            // 2. Test ProductosPanel
            System.out.println("\n[TEST] ProductosPanel...");
            ProductosPanel productos = new ProductosPanel();
            testCommand(productos, "CLEAR", "");
            testCommand(productos, "SET_FIELD", "PRECIO 100");
            testCommand(productos, "FILTER", "Cabello");
            productos.handleVoiceCommand("SEARCH", "Gel"); // Method call directly to verify fix
            System.out.println("[OK] ProductosPanel logic passed.");

            // 3. Test ProveedoresPanel (The one we fixed)
            System.out.println("\n[TEST] ProveedoresPanel...");
            ProveedoresPanel proveedores = new ProveedoresPanel();
            testCommand(proveedores, "SEARCH", "ProveedorX");
            testCommand(proveedores, "SELECT", "Proveedor1");
            System.out.println("[OK] ProveedoresPanel logic passed.");

            // 4. Test VentasPanel
            System.out.println("\n[TEST] VentasPanel...");
            VentasPanel ventas = new VentasPanel();
            testCommand(ventas, "CLEAR", "");
            testCommand(ventas, "AGREGA", "Gel"); // Logic should try to add
            testCommand(ventas, "AUMENTA", "5");
            testCommand(ventas, "SET_PAYMENT", "TARJETA");
            System.out.println("[OK] VentasPanel logic passed.");

            // 5. Test CitasPanel
            System.out.println("\n[TEST] CitasPanel...");
            CitasPanel citas = new CitasPanel();
            testCommand(citas, "CLEAR", "");
            testCommand(citas, "SET_FIELD", "FECHA 2025-01-01");
            // AGENDAR triggers dialog if invalid fields, so we skip it to avoid block
            System.out.println("[OK] CitasPanel logic passed.");

            System.out.println("\n=== TODOS LOS COMANDOS VERIFICADOS ===");
            System.exit(0);

        } catch (Exception e) {
            System.err.println("[FAIL] Exception during test: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void testCommand(VoiceAware panel, String cmd, String args) {
        System.out.print("  -> Executing " + cmd + " [" + args + "] ... ");
        try {
            panel.handleVoiceCommand(cmd, args);
            System.out.println("OK");
        } catch (Exception e) {
            System.out.println("FAIL (" + e.getMessage() + ")");
            throw e;
        }
    }
}
