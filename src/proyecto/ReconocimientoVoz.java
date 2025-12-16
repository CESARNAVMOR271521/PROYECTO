package proyecto;

import java.io.IOException;
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

    private Model model;
    private boolean running = false;

    public ReconocimientoVoz() {
        // Desactivar logs de Vosk para no ensuciar la consola
        LibVosk.setLogLevel(LogLevel.WARNINGS);

        try {
            // Cargar el modelo
            // Asegúrate de que la ruta sea correcta relativa al proyecto o absoluta
            model = new Model("vosk-model-small-es-0.42");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al cargar el modelo de voz: " + e.getMessage());
        }
    }

    public void escuchar() {
        if (model == null)
            return;

        running = true;

        // Formato de audio requerido por Vosk (16kHz, 16bit, mono)
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000, 16, 1, 2, 16000, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            System.err.println("Line not supported");
            return;
        }

        try (TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
                Recognizer recognizer = new Recognizer(model, 16000)) {

            line.open(format);
            line.start();

            System.out.println("Escuchando... (Diga 'salir' para detener)");

            int nbytes;
            byte[] b = new byte[4096];

            while (running) {
                nbytes = line.read(b, 0, b.length);

                if (nbytes >= 0) {
                    if (recognizer.acceptWaveForm(b, nbytes)) {
                        String result = recognizer.getResult();
                        System.out.println("Reconocido: " + result);
                        // Aquí podrías procesar el texto (result es un JSON)
                        // Ejemplo simple de parseo:
                        if (result.contains("articulo") || result.contains("salir")) {
                            // Lógica simple para detener si se desea controlar por voz
                        }
                    } else {
                        // System.out.println(recognizer.getPartialResult());
                    }
                }
            }

            line.stop();
            line.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void detener() {
        running = false;
    }

    public static void main(String[] args) {
        ReconocimientoVoz voz = new ReconocimientoVoz();
        voz.escuchar();
    }
}
