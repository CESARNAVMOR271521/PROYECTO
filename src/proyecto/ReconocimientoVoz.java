package proyecto;

import java.io.IOException;
import java.util.function.Consumer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JOptionPane;

import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

public class ReconocimientoVoz {

    private static ReconocimientoVoz instance;
    private Model model;
    private boolean running = false;
    private Thread listeningThread;

    private ReconocimientoVoz() {
        // Desactivar logs de Vosk para no ensuciar la consola
        LibVosk.setLogLevel(LogLevel.WARNINGS);

        try {
            // Cargar el modelo
            String modelPath = "vosk-model-small-es-0.42";
            System.out.println("Cargando modelo de voz desde: " + new java.io.File(modelPath).getAbsolutePath());
            model = new Model(modelPath);
            System.out.println("Modelo cargado correctamente.");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error al cargar el modelo de voz (vosk-model-small-es-0.42): " + e.getMessage());
        }
    }

    public boolean isReady() {
        return model != null;
    }

    public static synchronized ReconocimientoVoz getInstance() {
        if (instance == null) {
            instance = new ReconocimientoVoz();
        }
        return instance;
    }

    public void startListening(Consumer<String> onTextRecognized) {
        if (model == null) {
            System.err.println("No se puede iniciar: Modelo no cargado.");
            return;
        }
        if (running)
            return;

        running = true;
        listeningThread = new Thread(() -> {
            try {
                AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000, 16, 1, 2, 16000, false);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

                if (!AudioSystem.isLineSupported(info)) {
                    System.err.println("Line not supported");
                    running = false;
                    return;
                }

                try (TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
                        Recognizer recognizer = new Recognizer(model, 16000)) {

                    line.open(format);
                    line.start();

                    System.out.println("Escuchando...");

                    int nbytes;
                    byte[] b = new byte[4096];

                    while (running) {
                        nbytes = line.read(b, 0, b.length);

                        if (nbytes >= 0) {
                            if (recognizer.acceptWaveForm(b, nbytes)) {
                                String resultJson = recognizer.getResult();
                                processResult(resultJson, onTextRecognized);
                            } else {
                                // Partial results
                            }
                        }
                    }

                    line.stop();
                    line.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } finally {
                running = false;
                System.out.println("Detenido.");
            }
        });
        listeningThread.start();
    }

    private void processResult(String json, Consumer<String> callback) {
        // Manual JSON parsing: {"text" : "some text"}
        try {
            int textIndex = json.indexOf("\"text\"");
            if (textIndex != -1) {
                int colonIndex = json.indexOf(":", textIndex);
                if (colonIndex != -1) {
                    int startQuote = json.indexOf("\"", colonIndex);
                    if (startQuote != -1) {
                        int endQuote = json.indexOf("\"", startQuote + 1);
                        if (endQuote != -1) {
                            String text = json.substring(startQuote + 1, endQuote);
                            if (!text.trim().isEmpty()) {
                                callback.accept(text);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopListening() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}
