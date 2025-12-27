package proyecto.prueba;

import proyecto.ReconocimientoVoz;

public class TestVozModel {
    public static void main(String[] args) {
        System.out.println("Testing Vocal Model Loading...");
        ReconocimientoVoz voz = ReconocimientoVoz.getInstance();
        if (voz.isReady()) {
            System.out.println("SUCCESS: Model loaded successfully.");
        } else {
            System.out.println("FAILURE: Model failed to load.");
        }
        System.exit(0); // Ensure JVM exits
    }
}
