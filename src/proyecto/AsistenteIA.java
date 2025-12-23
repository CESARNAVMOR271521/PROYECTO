package proyecto;

import proyecto.util.OllamaClient;
import javax.swing.SwingUtilities;

public class AsistenteIA {

    private BarberiaChupirules mainApp;
    private OllamaClient aiClient;
    private boolean active;

    private static final String SYSTEM_PROMPT = 
        "Eres un asistente de navegación para una aplicación de Barbería. " +
        "Tu trabajo es interpretar comandos de voz y devolver el CÓDIGO del módulo correspondiente. " +
        "Los códigos válidos son: CLIENTES, BARBEROS, SERVICIOS, CITAS, VENTAS, DETALLE, PRODUCTOS, USUARIOS, PAGOS, FACTURAS, PROVEEDORES, COMPRAS, VOZ_LOGS. " +
        "Si el usuario pide salir, devuelve SALIR. " +
        "Si el usuario saluda o dice algo irrelevante, devuelve NULL. " +
        "Responde ÚNICAMENTE con el código, sin explicación ni texto adicional. " +
        "Ejemplo: 'Ir a clientes' -> CLIENTES. 'Quiero agendar una cita' -> CITAS.";

    public AsistenteIA(BarberiaChupirules app) {
        this.mainApp = app;
        this.aiClient = new OllamaClient();
        this.active = true;
    }

    public void iniciarEscucha() {
        ReconocimientoVoz.getInstance().startListening(texto -> {
            if (!active) return;
            System.out.println("Voz detectada: " + texto);
            procesarComando(texto);
        });
    }

    private void procesarComando(String textoUsuario) {
        // Ejecutar en hilo separado para no bloquear UI
        new Thread(() -> {
            String prompt = SYSTEM_PROMPT + "\nUsuario: " + textoUsuario + "\nAsistente:";
            String respuesta = aiClient.sendPrompt(prompt);
            System.out.println("IA Respondió: " + respuesta);

            final String comando = respuesta.trim().toUpperCase().replace(".", "");

            SwingUtilities.invokeLater(() -> {
                ejecutarAccion(comando);
            });
        }).start();
    }

    private void ejecutarAccion(String comando) {
        if (comando.equals("NULL") || comando.isEmpty()) return;
        
        if (comando.equals("SALIR")) {
            System.exit(0);
        }

        try {
            // Intentar navegar
            mainApp.setModuleActive(comando);
            System.out.println("Navegando a: " + comando);
        } catch (Exception e) {
            System.err.println("Error al navegar: " + e.getMessage());
        }
    }
    
    public void detener() {
        this.active = false;
        ReconocimientoVoz.getInstance().stopListening();
    }
}
