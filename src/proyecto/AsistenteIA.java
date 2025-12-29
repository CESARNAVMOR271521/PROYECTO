package proyecto;

import proyecto.util.OllamaClient;
import javax.swing.SwingUtilities;

public class AsistenteIA {

    private BarberiaChupirules mainApp;
    private OllamaClient aiClient;
    private boolean active;

    private static final String SYSTEM_PROMPT = 
        "Eres un asistente operativo para la barbería. " +
        "Tu misión es extraer la intención del usuario y convertirla en comando. " +
        "COMANDOS VÁLIDOS: " +
        "- NAVIGATE [MODULO] (ej. 'Ve a clientes', 'Abre ventas') " +
        "- CREATE (ej. 'Agrega nuevo', 'Nuevo cliente', 'Crear cita') " + 
        "- UPDATE (ej. 'Actualiza', 'Modificar', 'Editar') " +
        "- DELETE (ej. 'Borra', 'Elimina', 'Cancela') " +
        "- CLEAR (ej. 'Limpia', 'Borrar formulario') " +
        "- SEARCH [QUERY] (ej. 'Busca a Juan', 'Filtra por corte') " +
        "- SELECT [QUERY] (ej. 'Selecciona a Juan', 'Elige corte de pelo') " +
        "- SET_FIELD [FIELD] [VALUE] (ej. 'Nombre Juan', 'Telefono 555') " +
        "- AGREGA [ITEM] (Ventas: 'Agrega corte') " +
        "- AUMENTA [CANTIDAD] (Ventas: 'Aumenta a 2') " +
        "- REGISTRA (Ventas: 'Cobrar') " +
        "- AGENDAR [ARGS] (Citas: 'Cita para Juan mañana a las 5') " + 
        "Formato de respuesta: COMANDO [ARGUMENTOS]";

    public AsistenteIA(BarberiaChupirules app) {
        this.mainApp = app;
        this.aiClient = new OllamaClient();
        this.active = true;
    }

    public void iniciarEscucha() {
        ReconocimientoVoz.getInstance().startListening(texto -> {
            if (!active) return;
            System.out.println("Voz detectada: " + texto);
            // Use hybrid processing
            procesarHibrido(texto);
        });
    }

    private void procesarComando(String textoUsuario) {
        new Thread(() -> {
            // Feedback inmediato de "Pensando..."
            // TextToSpeech.speak("Procesando..."); 

            String prompt = SYSTEM_PROMPT + "\nUsuario: " + textoUsuario + "\nAsistente:";
            String respuesta = aiClient.sendPrompt(prompt);
            System.out.println("IA de Chupirules respondió: " + respuesta);

            String cleanResponse = respuesta.replaceAll("\\*\\*", "")
                                           .replaceAll("`", "")
                                           .trim();
            
            final String comandoFull = cleanResponse;
            
            SwingUtilities.invokeLater(() -> {
                interpretarYEjecutar(comandoFull, textoUsuario);
            });
        }).start();
    }

    // Hybrid Parsing: Regex first for speed, then AI for complexity
    public void procesarHibrido(String textoUsuario) {
        String upper = removeAccents(textoUsuario.toUpperCase());

        // 1. Navigation Fast Path
        if (upper.startsWith("VE A ") || upper.startsWith("ABRE ") || upper.startsWith("PASA A ") || upper.startsWith("IR A ")) {
             String dest = upper.replace("VE A ", "").replace("ABRE ", "").replace("PASA A ", "").replace("LA VENTANA ", "").replace("IR A ", "").trim();
             String modulo = mapModule(dest);
             if (modulo != null) {
                 SwingUtilities.invokeLater(() -> {
                     mainApp.setModuleActive(modulo);
                     TextToSpeech.speak("Accediendo a " + dest.toLowerCase());
                 });
                 return;
             }
        }
        
        // 2. Select Fast Path
        if (upper.startsWith("SELECCIONA ") || upper.startsWith("ELIGE ") || upper.startsWith("BUSCA ")) {
            String query = upper.replace("SELECCIONA ", "").replace("ELIGE ", "").replace("BUSCA A ", "").replace("BUSCA ", "").replace("AL CLIENTE ", "").trim();
            final String fQuery = query; 
            SwingUtilities.invokeLater(() -> mainApp.dispatchVoiceCommand(null, "SELECT", fQuery));
            return;
        }

        // 3. Simple Actions Fast Path
        if (upper.contains("LIMPIA") || upper.contains("BORRAR FORMULARIO") || upper.equals("NUEVO")) {
             SwingUtilities.invokeLater(() -> mainApp.dispatchVoiceCommand(null, "CLEAR", ""));
             return;
        }
        if (upper.contains("ACTUALIZA") || upper.contains("GUARDAR CAMBIOS") || upper.contains("MODIFICA") || upper.startsWith("EDITA")) {
             SwingUtilities.invokeLater(() -> mainApp.dispatchVoiceCommand(null, "UPDATE", ""));
             return;
        }
        if (upper.equals("AGREGA") || upper.equals("CREAR") || upper.equals("GUARDAR") || upper.startsWith("AGREGAR")) {
             SwingUtilities.invokeLater(() -> mainApp.dispatchVoiceCommand(null, "CREATE", ""));
             return;
        }
        if (upper.contains("BORRA") || upper.contains("ELIMINA")) {
             SwingUtilities.invokeLater(() -> mainApp.dispatchVoiceCommand(null, "DELETE", ""));
             return;
        }
        
        // 4. Field Setting
        // Normalize field names
        if (upper.startsWith("NOMBRE ") || upper.startsWith("TELEFONO ") || upper.startsWith("CORREO ") || 
            upper.startsWith("PRECIO ") || upper.startsWith("STOCK ") || upper.startsWith("COSTO ") || 
            upper.startsWith("HISTORIAL ") || upper.startsWith("NOTA ") || upper.startsWith("NOTAS ")) {
             
             String[] parts = upper.split(" ", 2);
             if (parts.length > 1) {
                 String field = parts[0];
                 String value = parts[1]; 
                 
                 // Normalize phone numbers
                 if (field.startsWith("TELEFONO")) {
                     value = normalizeNumbers(value).replaceAll("[^0-9]", "");
                 }
                 
                 // Normalize Historial/Nota to HISTORIAL for switching
                 if (field.startsWith("NOTA")) field = "HISTORIAL";
                 
                 final String fField = field;
                 final String fValue = value; 
                 SwingUtilities.invokeLater(() -> mainApp.dispatchVoiceCommand(null, "SET_FIELD", fField + " " + fValue));
                 return;
             }
        }

        // 5. Fallback to AI
        procesarComando(textoUsuario);
    }

    private String removeAccents(String text) {
        return java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD)
               .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
    
    // Simple helper to convert basic Spanish number words to digits if needed
    private String normalizeNumbers(String text) {
        return text.replace("UNO", "1").replace("DOS", "2").replace("TRES", "3")
                   .replace("CUATRO", "4").replace("CINCO", "5").replace("SEIS", "6")
                   .replace("SIETE", "7").replace("OCHO", "8").replace("NUEVE", "9")
                   .replace("CERO", "0");
    }
    
    private String mapModule(String input) {
        if (input.contains("CLIENT")) return "CLIENTES";
        if (input.contains("BARBER") || input.contains("EMPLEADO")) return "BARBEROS";
        if (input.contains("SERVIC")) return "SERVICIOS";
        if (input.contains("CITA") || input.contains("AGENDA")) return "CITAS";
        if (input.contains("VENTA") || input.contains("PUNTO")) return "VENTAS";
        if (input.contains("PRODUCT") || input.contains("INVENTARIO")) return "PRODUCTOS";
        if (input.contains("HISTORIAL")) return "DETALLE";
        if (input.contains("USUARIO")) return "USUARIOS";
        if (input.contains("PAGO")) return "PAGOS";
        if (input.contains("FACTURA")) return "FACTURAS";
        if (input.contains("PROVEEDOR")) return "PROVEEDORES";
        if (input.contains("COMPRA")) return "COMPRAS";
        if (input.contains("VOZ")) return "VOZ_LOGS";
        if (input.contains("INICIO") || input.contains("CASA")) return "INICIO";
        return null;
    }

    private void interpretarYEjecutar(String respuestaIA, String textoOriginal) {
        String[] parts = respuestaIA.split(" ", 2);
        String comando = parts[0].toUpperCase();
        String args = parts.length > 1 ? parts[1] : "";

        if (comando.equals("NULL") || comando.isEmpty()) return;

        if (comando.equals("SALIR")) {
            TextToSpeech.speak("Hasta luego.");
            System.exit(0);
            return;
        }

        if (comando.equals("NAVIGATE") || isNavigationCommand(comando)) {
             String target = isNavigationCommand(comando) ? comando : mapModule(args);
             if (target != null) {
                 mainApp.setModuleActive(target);
                 TextToSpeech.speak("Navegando.");
             }
        } else {
             // Dispatch generic command
             mainApp.dispatchVoiceCommand(null, comando, args);
        }
    }

    private boolean isNavigationCommand(String cmd) {
        String[] validos = {"CLIENTES", "BARBEROS", "SERVICIOS", "CITAS", "VENTAS", "DETALLE", "PRODUCTOS", "USUARIOS", "PAGOS", "FACTURAS", "PROVEEDORES", "COMPRAS"};
        for (String v : validos) if (v.equals(cmd)) return true;
        return false;
    }
    
    public void detener() {
        this.active = false;
        ReconocimientoVoz.getInstance().stopListening();
    }
}
