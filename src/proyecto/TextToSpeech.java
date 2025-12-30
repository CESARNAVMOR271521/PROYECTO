package proyecto;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

/**
 * Provides Text-to-Speech functionality using FreeTTS.
 */
public class TextToSpeech {

    private static Voice voice;

    public static void speak(String text) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }

        new Thread(() -> {
            try {
                // PAUSE LISTENING to avoid self-hearing
                ReconocimientoVoz.getInstance().pauseListening();

                if (voice == null) {
                    // Ensure the voice directory is found
                    System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
                    
                    VoiceManager voiceManager = VoiceManager.getInstance();
                    voice = voiceManager.getVoice("kevin16");
                    
                    if (voice == null) {
                         System.out.println("Voice 'kevin16' not found, trying 'kevin'");
                        voice = voiceManager.getVoice("kevin");
                    }
                    
                    if (voice != null) {
                        voice.allocate();
                    } else {
                        System.err.println("Cannot find a voice named 'kevin16' or 'kevin'");
                        return;
                    }
                }
                
                voice.speak(text);
                
            } catch (Exception e) {
                System.err.println("Error executing FreeTTS: " + e.getMessage());
                e.printStackTrace();
            } finally {
                // RESUME LISTENING
                ReconocimientoVoz.getInstance().resumeListening();
            }
        }).start();
    }
}
