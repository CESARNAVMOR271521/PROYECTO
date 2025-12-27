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

            // Limpieza de respuesta (quitar markdown, espacios extra)
            String cleanResponse = respuesta.replaceAll("\\*\\*", "") // quitar negritas markdown
                                           .replaceAll("`", "")      // quitar codigo markdown
                                           .trim();
            
            // Buscar la palabra clave
            final String comando = extraerComando(cleanResponse);

            SwingUtilities.invokeLater(() -> {
                ejecutarAccion(comando);
            });
        }).start();
    }

    private String extraerComando(String response) {
        String upper = response.toUpperCase();
        
        // Lista de comandos válidos para buscar en la respuesta si la IA es muy dicharachera
        String[] validos = {"CLIENTES", "BARBEROS", "SERVICIOS", "CITAS", "VENTAS", "DETALLE", "PRODUCTOS", "USUARIOS", "PAGOS", "FACTURAS", "PROVEEDORES", "COMPRAS", "VOZ_LOGS", "SALIR"};
        
        for (String cmd : validos) {
            if (upper.contains(cmd)) {
                return cmd;
            }
        }
        
        return "NULL";
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
            
            // Voice Feedback
            TextToSpeech.speak("Abriendo módulo " + comando.toLowerCase());
        } catch (Exception e) {
            System.err.println("Error al navegar: " + e.getMessage());
            TextToSpeech.speak("No pude encontrar el módulo solicitado.");
        }
    }
    
    public void detener() {
        this.active = false;
        ReconocimientoVoz.getInstance().stopListening();
    }
}
