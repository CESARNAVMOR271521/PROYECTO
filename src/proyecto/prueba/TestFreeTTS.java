package proyecto.prueba;

import proyecto.TextToSpeech;

public class TestFreeTTS {
    public static void main(String[] args) {
        System.out.println("Testing FreeTTS...");
        try {
            TextToSpeech.speak("Hello, this is a test of the FreeTTS system.");
            // Give it some time to speak as it runs in a thread
            Thread.sleep(3000); 
            System.out.println("Test command sent.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
