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

    private proyecto.dao.RegistroVozDAO logDao;

    public AsistenteIA(BarberiaChupirules app) {
        this.mainApp = app;
        this.aiClient = new OllamaClient();
        this.active = true;
        this.logDao = new proyecto.dao.RegistroVozDAO();
    }

    public void iniciarEscucha() {
        ReconocimientoVoz.getInstance().startListening(texto -> {
            if (!active) return;
            System.out.println("Voz detectada: " + texto);
            // Log to DB
            logDao.insertar(texto);
            
            // Use hybrid processing
            procesarHibrido(texto);
        });
    }

    private void procesarComando(String textoUsuario) {
        new Thread(() -> {
            // Feedback inmediato de "Pensando..."
            try {
                 TextToSpeech.speak("Procesando");
            } catch (Exception e) {} 

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



    // Helper to convert symbols
    private String normalizeSymbols(String text) {
        String t = text.toUpperCase();
        // Replace words with symbols, handling spaces
        t = t.replaceAll("\\bARROBA\\b", "@");
        t = t.replaceAll("\\bPUNTO\\b", ".");
        t = t.replaceAll("\\bCOMA\\b", ",");
        t = t.replaceAll("\\bGUION\\b", "-");
        t = t.replaceAll("\\bGUION BAJO\\b", "_");
        
        // Remove spaces around the symbol if needed (e.g. "email arroba gmail" -> "email@gmail")
        // This is tricky without hurting normal text, but let's try strict removal for common cases
        t = t.replace(" @ ", "@").replace(" . ", ".");
        return t;
    }

    // Hybrid Parsing: Regex first for speed, then AI for complexity
    public void procesarHibrido(String textoUsuario) {
        // First remove accents
        String upper = removeAccents(textoUsuario.toUpperCase());
        // Then normalize symbols and numbers globally
        upper = normalizeNumbers(upper);
        upper = normalizeSymbols(upper); 
        
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
        
        // 2. Select Fast Path - User prefers "BUSCA"
        if (upper.startsWith("SELECCIONA ") || upper.startsWith("ELIGE ") || upper.startsWith("BUSCA ")) {
            String query = upper.replace("SELECCIONA ", "").replace("ELIGE ", "").replace("BUSCA A ", "").replace("BUSCA ", "").replace("AL CLIENTE ", "").replace("AL BARBERO ", "").trim();
            final String fQuery = query; 
            SwingUtilities.invokeLater(() -> mainApp.dispatchVoiceCommand(null, "SELECT", fQuery)); // Map BUSCA to SELECT internally
            return;
        }

        // 3. Simple Actions Fast Path
        // "NUEVO" or "LIMPIAR" -> CLEAR (Prepare for new entry)
        if (upper.contains("LIMPIA") || upper.contains("BORRAR FORMULARIO") || upper.startsWith("NUEVO")) {
             SwingUtilities.invokeLater(() -> mainApp.dispatchVoiceCommand(null, "CLEAR", ""));
             TextToSpeech.speak("Formulario limpio.");
             return;
        }

        // "AGREGA" / "GUARDAR" -> CREATE (Save to DB)
        if (upper.startsWith("AGREGA") || upper.startsWith("CREAR") || upper.startsWith("GUARDAR") || upper.startsWith("REGISTRAR")) {
             SwingUtilities.invokeLater(() -> {
                 mainApp.dispatchVoiceCommand(null, "CREATE", "");
                 TextToSpeech.speak("Creando registro.");
             });
             return;
        }

        if (upper.contains("ACTUALIZA") || upper.contains("GUARDAR CAMBIOS") || upper.contains("MODIFICA") || upper.startsWith("EDITA")) {
             SwingUtilities.invokeLater(() -> {
                 mainApp.dispatchVoiceCommand(null, "UPDATE", "");
                 TextToSpeech.speak("Actualizando.");
             });
             return;
        }

        if (upper.contains("BORRA") || upper.contains("ELIMINA")) {
             SwingUtilities.invokeLater(() -> {
                 mainApp.dispatchVoiceCommand(null, "DELETE", "");
                 TextToSpeech.speak("Eliminando.");
             });
             return;
        }

        // New: Payment Method
        if (upper.contains("PAGO CON") || upper.contains("PAGAR CON") || upper.contains("CAMBIA A EFECTIVO") || upper.contains("CAMBIA A TARJETA")) {
            String method = (upper.contains("TARJETA")) ? "TARJETA" : "EFECTIVO";
            SwingUtilities.invokeLater(() -> {
                mainApp.dispatchVoiceCommand(null, "SET_PAYMENT", method);
                TextToSpeech.speak("Pago con " + method.toLowerCase());
            });
            return;
        }

        // New: Process Sale / Finish
        if (upper.startsWith("COBRAR") || upper.contains("FINALIZAR VENTA") || upper.contains("IMPRIMIR TICKET") || upper.equals("TERMINAR")) {
            SwingUtilities.invokeLater(() -> {
                mainApp.dispatchVoiceCommand(null, "PROCESS_SALE", "");
                TextToSpeech.speak("Procesando venta.");
            });
            return;
        }

        // New: Filter / Search refinement
        if (upper.startsWith("FILTRA POR") || upper.startsWith("MUESTRAME SOLO")) {
            String filterArgs = upper.replace("FILTRA POR", "").replace("MUESTRAME SOLO", "").trim();
            SwingUtilities.invokeLater(() -> {
                mainApp.dispatchVoiceCommand(null, "FILTER", filterArgs);
                TextToSpeech.speak("Filtrando por " + filterArgs);
            });
            return;
        }
        
        // 4. Field Setting
        // Normalize field names
        if (upper.startsWith("PRECIO COMPRA ") || upper.startsWith("COSTO ")) {
            String val = upper.replace("PRECIO COMPRA ", "").replace("COSTO ", "");
             SwingUtilities.invokeLater(() -> {
                 mainApp.dispatchVoiceCommand(null, "SET_FIELD", "COSTO " + val);
                 TextToSpeech.speak("Costo actualizado.");
             });
             return;
        }

        if (upper.startsWith("NOMBRE ") || upper.startsWith("TELEFONO ") || upper.startsWith("CORREO ") || 
            upper.startsWith("PRECIO ") || upper.startsWith("STOCK ") || upper.startsWith("HISTORIAL ") || 
             upper.startsWith("NOTA ") || upper.startsWith("NOTAS ") ||
            upper.startsWith("DESCRIPCION ") || upper.startsWith("CATEGORIA ") || upper.startsWith("ESPECIALIDAD") ||
            upper.startsWith("USUARIO ") || upper.startsWith("PASSWORD ") || upper.startsWith("CONTRA") ||
            upper.startsWith("VENTA ") || upper.startsWith("MINIMO ") || 
            upper.startsWith("ALERTA ") || upper.startsWith("CANTIDAD ") || upper.startsWith("EXISTENCIA ") ||
            upper.startsWith("INVENTARIO ") || upper.startsWith("DETALLE ")) {
             
             String[] parts = upper.split(" ", 2);
             if (parts.length > 1) {
                 String field = parts[0];
                 String value = parts[1]; 
                 
                 if (field.startsWith("TELEFONO")) {
                     value = value.replaceAll("[^0-9]", "");
                 }
                 
                 // Normalize Field Names for consistency
                 if (field.startsWith("NOTA")) field = "HISTORIAL";
                 if (field.startsWith("DESCR")) field = "DESCRIPCION";
                 if (field.startsWith("ESPEC")) field = "ESPECIALIDADES";
                 if (field.startsWith("CONTRA")) field = "PASSWORD";
                 
                 // Product fields normalization
                 if (field.startsWith("CANTIDAD") || field.startsWith("EXISTENCIA") || field.startsWith("INVENTARIO")) field = "STOCK";
                 if (field.startsWith("ALERTA")) field = "MINIMO";
                 if (field.startsWith("COMPRA")) field = "COSTO";
                 if (field.startsWith("VENTA")) field = "PRECIO"; // Maps to Venta
                 if (field.startsWith("DETALLE")) field = "DESCRIPCION";
                 
                 final String fField = field;
                 final String fValue = value; 
                 SwingUtilities.invokeLater(() -> {
                     mainApp.dispatchVoiceCommand(null, "SET_FIELD", fField + " " + fValue);
                     TextToSpeech.speak("Campo " + fField.toLowerCase() + " actualizado.");
                 });
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
    
    // Helper to convert Spanish number words to digits using Regex and Composite logic
    private String normalizeNumbers(String text) {
        // Pad text to ensure boundaries work at start/end
        String t = " " + text + " ";
        
        // 1. Basic map for standalone numbers
        String[][] replacements = {
            {"UNO", "1"}, {"DOS", "2"}, {"TRES", "3"}, {"CUATRO", "4"}, {"CINCO", "5"},
            {"SEIS", "6"}, {"SIETE", "7"}, {"OCHO", "8"}, {"NUEVE", "9"}, {"CERO", "0"},
            {"DIEZ", "10"}, {"ONCE", "11"}, {"DOCE", "12"}, {"TRECE", "13"}, {"CATORCE", "14"}, {"QUINCE", "15"},
            {"DIECISEIS", "16"}, {"DIECISIETE", "17"}, {"DIECIOCHO", "18"}, {"DIECINUEVE", "19"},
            {"VEINTE", "20"}, {"TREINTA", "30"}, {"CUARENTA", "40"}, {"CINCUENTA", "50"},
            {"SESENTA", "60"}, {"SETENTA", "70"}, {"OCHENTA", "80"}, {"NOVENTA", "90"}, 
            {"CIEN", "100"}, {"DOSCIENTOS", "200"}, {"TRESCIENTOS", "300"}, {"CUATROCIENTOS", "400"},
            {"QUINIENTOS", "500"}, {"SEISCIENTOS", "600"}, {"SETECIENTOS", "700"}, {"OCHOCIENTOS", "800"},
            {"NOVECIENTOS", "900"}, {"MIL", "1000"}
        };
        
        // Use non-word character look-arounds or explicit spaces for safer matching
        // We padded 't' with spaces, so we can match " WORD "
        for (String[] pair : replacements) {
            t = t.replaceAll("(?i)([^a-zA-Z0-9])" + pair[0] + "([^a-zA-Z0-9])", "$1" + pair[1] + "$2");
            // Run twice to handle overlapping matches like " UNO UNO " where shared space might block second match
             t = t.replaceAll("(?i)([^a-zA-Z0-9])" + pair[0] + "([^a-zA-Z0-9])", "$1" + pair[1] + "$2");
        }

        // 3. Handle "VEINTI..." composites (Veintiuno -> 21) BEFORE general regex
        t = t.replaceAll("(?i)VEINTIUNO", "21")
             .replaceAll("(?i)VEINTIDOS", "22")
             .replaceAll("(?i)VEINTITRES", "23") 
             .replaceAll("(?i)VEINTICUATRO", "24")
             .replaceAll("(?i)VEINTICINCO", "25")
             .replaceAll("(?i)VEINTISEIS", "26")
             .replaceAll("(?i)VEINTISIETE", "27")
             .replaceAll("(?i)VEINTIOCHO", "28")
             .replaceAll("(?i)VEINTINUEVE", "29");

        // 4. Handle Hundreds Composites: "DOSCIENTOS CINCUENTA" -> "200 50" -> "250"
        // Regex: (DIGIT ending in 00) + whitespace + (DIGIT 1-99)
        java.util.regex.Pattern pHundreds = java.util.regex.Pattern.compile("([1-9]00)\\s+([0-9]{1,2})");
        java.util.regex.Matcher mH = pHundreds.matcher(t);
        StringBuffer sbH = new StringBuffer();
        while (mH.find()) {
            try {
                int hundred = Integer.parseInt(mH.group(1));
                int rest = Integer.parseInt(mH.group(2));
                mH.appendReplacement(sbH, String.valueOf(hundred + rest));
            } catch (Exception e) {
                mH.appendReplacement(sbH, mH.group(0));
            }
        }
        mH.appendTail(sbH);
        t = sbH.toString();

        // 2. Handle Composites like "CINCUENTA Y CINCO" -> "50 Y 5" -> "55"
        // Regex: (DIGIT ending in 0) + whitespace + Y + whitespace + (DIGIT single)
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("([0-9]+0)\\s+Y\\s+([0-9])");
        java.util.regex.Matcher m = p.matcher(t);
        
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            try {
                int ten = Integer.parseInt(m.group(1));
                int unit = Integer.parseInt(m.group(2));
                m.appendReplacement(sb, String.valueOf(ten + unit));
            } catch (Exception e) {
                m.appendReplacement(sb, m.group(0));
            }
        }
        m.appendTail(sb);
        t = sb.toString();
        
        return t.trim();
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
        if (input.contains("CONFIG")) return "CONFIG"; // Added configuration
        return null;
    }

    private void interpretarYEjecutar(String respuestaIA, String textoOriginal) {
        String[] parts = respuestaIA.split(" ", 2);
        String comando = parts[0].toUpperCase();
        String args = parts.length > 1 ? parts[1] : "";

        if (comando.equals("NULL") || comando.isEmpty()) return;
        
        // Custom exit phrase
        if (respuestaIA.contains("SALIR PROGRAMA") || comando.equals("SALIR")) {
            TextToSpeech.speak("Cerrando programa. Hasta luego.");
            try { Thread.sleep(1500); } catch (InterruptedException e) {}
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
