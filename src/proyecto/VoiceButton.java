package proyecto;

import java.awt.Color;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import proyecto.util.Theme;

public class VoiceButton extends JButton {

    private boolean listening = false;
    private final Color LISTENING_COLOR = Theme.COLOR_ACCENT_RED;
    private final Color DEFAULT_COLOR = Theme.COLOR_ACCENT_GOLD;
    private Component lastFocusedComponent;

    public VoiceButton() {
        super("🎤"); // Microphone emoji or text
        setBackground(DEFAULT_COLOR);
        setForeground(Theme.COLOR_PRIMARY);
        setFocusable(false); 

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
               // Fallback: Search siblings for a JTextField
               if (getParent() != null) {
                   for (Component c : getParent().getComponents()) {
                       if (c instanceof JTextField) {
                           lastFocusedComponent = c;
                           break;
                       }
                   }
               }
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
                            // Restore focus to the target field so user can keep typing if needed
                            targetField.requestFocusInWindow();
                        }
                    });
                });
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "No se encontró un campo de texto para escribir.\nPor favor, haga clic en un campo de texto antes de activar el micrófono.",
                    "Error de Foco", javax.swing.JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // Optional: Allow manual setting of target if needed
    public void setTargetComponent(Component c) {
        this.lastFocusedComponent = c;
    }
}
