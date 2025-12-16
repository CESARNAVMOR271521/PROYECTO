package proyecto;

import java.awt.Color;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class VoiceButton extends JButton {

    private boolean listening = false;
    private final Color LISTENING_COLOR = new Color(255, 100, 100);
    private final Color DEFAULT_COLOR = new Color(199, 179, 106); // Matching other buttons
    private Component lastFocusedComponent;

    public VoiceButton() {
        super("🎤"); // Microphone emoji or text
        setBackground(DEFAULT_COLOR);
        setFocusable(false); // Important: don't steal focus when clicked so we can remember previous
                             // component

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleListening();
            }
        });
    }

    private void toggleListening() {
        ReconocimientoVoz service = ReconocimientoVoz.getInstance();

        if (listening) {
            // Stop listening
            service.stopListening();
            setText("🎤");
            setBackground(DEFAULT_COLOR);
            listening = false;
        } else {
            if (!service.isReady()) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "El servicio de voz no está listo (Modelo no cargado).");
                return;
            }
            // Start listening
            // Determine target text field
            Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            if (focusOwner instanceof JTextField) {
                lastFocusedComponent = focusOwner;
            } else if (lastFocusedComponent == null || !(lastFocusedComponent instanceof JTextField)) {
                // If we don't have a tracked focus, we might want to just return or find one
                // manually
                // For now, let's assume the user clicked a text field recently
                // System.out.println("No text field focused");
                // We proceed anyway, but maybe the text goes nowhere if lastFocusedComponent is
                // null?
                // Let's rely on `lastFocusedComponent` which might need updating via
                // FocusListener elsewhere
                // However, since this button is setFocusable(false), clicking it shouldn't
                // completely remove focus from the window,
                // BUT the focus owner might become null temporarily during click.
                // Ideally, track focus globally or assume the last known text field.
            }

            final JTextField targetField = (lastFocusedComponent instanceof JTextField)
                    ? (JTextField) lastFocusedComponent
                    : null;

            if (targetField != null) {
                listening = true;
                setText("🛑");
                setBackground(LISTENING_COLOR);

                service.startListening(text -> {
                    // Save to log
                    new proyecto.dao.RegistroVozDAO().insertar(text);

                    SwingUtilities.invokeLater(() -> {
                        if (targetField.isEnabled() && targetField.isEditable()) {
                            String current = targetField.getText();
                            if (!current.isEmpty() && !current.endsWith(" ")) {
                                current += " ";
                            }
                            targetField.setText(current + text);
                        }
                    });
                });
            } else {
                // Warning or just don't start
                System.out.println("No target text area found to type into.");
            }
        }
    }

    // Optional: Allow manual setting of target if needed
    public void setTargetComponent(Component c) {
        this.lastFocusedComponent = c;
    }
}
