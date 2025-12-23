package proyecto.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class OllamaClient {

    private static final String API_URL = "http://localhost:11434/api/generate";
    private static final String MODEL = "gemma3:12b"; 

    public String sendPrompt(String promptText) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Escapar comillas y saltos de línea para JSON manual simple
            String escapedPrompt = promptText.replace("\"", "\\\"").replace("\n", "\\n");

            String jsonInputString = "{"
                    + "\"model\": \"" + MODEL + "\","
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
                result.append(c);
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
        
        // Decodificar escapes comunes de JSON si es necesario (\n, \t, etc)
        return result.toString().replace("\\n", "\n").replace("\\t", "\t").replace("\\\"", "\"");
    }
}
