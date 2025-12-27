package proyecto.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class OllamaClient {

    private static final String API_GENERATE = "http://localhost:11434/api/generate";
    private static final String API_TAGS = "http://localhost:11434/api/tags";
    
    private String modelName = null;

    public OllamaClient() {
        // Al instanciar, intentamos detectar el modelo
        this.modelName = getAvailableModel();
        if (this.modelName == null) {
            System.err.println("ADVERTENCIA: No se detectó ningún modelo en Ollama.");
        } else {
            System.out.println("OllamaClient usará el modelo: " + this.modelName);
        }
    }

    private String getAvailableModel() {
        try {
            URL url = new URL(API_TAGS);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            
            if (conn.getResponseCode() != 200) {
                return null;
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                
                // Parseo manual simple de JSON para encontrar el primer "name": "..."
                String json = response.toString();
                // Buscar "name":"
                String key = "\"name\":\"";
                int start = json.indexOf(key);
                if (start != -1) {
                    start += key.length();
                    int end = json.indexOf("\"", start);
                    if (end != -1) {
                        return json.substring(start, end);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error buscando modelos: " + e.getMessage());
        }
        return null; // Fallback o null
    }

    public String sendPrompt(String promptText) {
        if (modelName == null) {
            // Reintentar por si se cargó después
            modelName = getAvailableModel();
            if (modelName == null) {
                return "Error: No hay modelo AI disponible (Ollama no responde o sin modelos).";
            }
        }

        try {
            URL url = new URL(API_GENERATE);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Escapar comillas y saltos de línea para JSON manual simple
            String escapedPrompt = promptText.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");

            String jsonInputString = "{"
                    + "\"model\": \"" + modelName + "\","
                    + "\"prompt\": \"" + escapedPrompt + "\","
                    + "\"stream\": false"
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            if (code != 200) {
                return "Error: Server returned code " + code;
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return parseResponse(response.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    private String parseResponse(String json) {
        // Búsqueda manual del campo "response"
        // Formato esperado: "response":"texto..."
        String key = "\"response\":\"";
        int start = json.indexOf(key);
        if (start == -1) return "";
        
        start += key.length();
        
        // Buscar el cierre de comillas, teniendo en cuenta escapes básicos
        StringBuilder result = new StringBuilder();
        boolean escape = false;
        
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            
            if (escape) {
                // Manejar caracteres escapados
                if (c == 'n') result.append('\n');
                else if (c == 't') result.append('\t');
                else if (c == 'r') {} // ignorar
                else result.append(c); // comillas, barras, etc
                escape = false;
            } else {
                if (c == '\\') {
                    escape = true;
                } else if (c == '"') {
                    // Fin del string
                    break;
                } else {
                    result.append(c);
                }
            }
        }
        
        return result.toString();
    }
}
