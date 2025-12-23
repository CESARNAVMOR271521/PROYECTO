package proyecto.prueba;

import proyecto.util.OllamaClient;

public class TestOllama {
    public static void main(String[] args) {
        System.out.println("Probando conexión con Ollama...");
        OllamaClient client = new OllamaClient();
        String prompt = "Responde solo con la palabra CONFIRMADO si recibes esto.";
        
        long start = System.currentTimeMillis();
        String response = client.sendPrompt(prompt);
        long end = System.currentTimeMillis();
        
        System.out.println("Respuesta recibida: " + response);
        System.out.println("Tiempo: " + (end - start) + "ms");
        
        if (response.toUpperCase().contains("CONFIRMADO")) {
            System.out.println("✅ CONEXIÓN EXITOSA");
        } else {
            System.out.println("❌ RESPUESTA INESPERADA O ERROR");
        }
    }
}
