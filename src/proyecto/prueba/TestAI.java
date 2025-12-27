package proyecto.prueba;

import proyecto.util.OllamaClient;

public class TestAI {
    public static void main(String[] args) {
        System.out.println("=== TEST CONEXIÓN IA ===");
        
        OllamaClient client = new OllamaClient();
        
        // 1. Probar un prompt simple
        String prompt = "Responde solo con la palabra: HOLA";
        System.out.println("Enviando prompt: " + prompt);
        
        long start = System.currentTimeMillis();
        String response = client.sendPrompt(prompt);
        long end = System.currentTimeMillis();
        
        System.out.println("Respuesta recibida en " + (end - start) + "ms");
        System.out.println("Contenido: [" + response + "]");
        
        if (response.toUpperCase().contains("HOLA")) {
            System.out.println("✅ PRUEBA EXITOSA: La IA respondió correctamente.");
        } else {
            System.out.println("❌ PRUEBA FALLIDA: Respuesta inesperada.");
        }
    }
}
