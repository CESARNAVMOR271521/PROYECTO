package proyecto;

import java.io.IOException;

/**
 * Provides Text-to-Speech functionality using Native Windows commands (PowerShell).
 * This avoids the need for external libraries like MaryTTS, using
 * System.Speech.Synthesis.SpeechSynthesizer built into Windows.
 */
public class TextToSpeech {

    public static void speak(String text) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }

        // Escape double quotes for PowerShell
        String escapedText = text.replace("\"", "\\\"");

        // PowerShell command to speak
        // Add-Type -AssemblyName System.Speech; $synth = New-Object System.Speech.Synthesis.SpeechSynthesizer; $synth.Speak("Hello")
        String command = String.format(
                "powershell.exe -Command \"Add-Type -AssemblyName System.Speech; " +
                        "$synth = New-Object System.Speech.Synthesis.SpeechSynthesizer; " +
                        "$synth.Speak(\\\"%s\\\");\"",
                escapedText);

        new Thread(() -> {
            try {
                // Execute command without opening a window if possible, but Runtime.exec matches
                // standard behavior
                Process p = Runtime.getRuntime().exec(command);
                p.waitFor();
            } catch (IOException | InterruptedException e) {
                System.err.println("Error executing TTS: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}
