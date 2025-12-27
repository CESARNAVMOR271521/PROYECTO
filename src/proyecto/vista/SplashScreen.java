package proyecto.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import proyecto.util.Theme;

/**
 * Pantalla de carga (Splash Screen) con barra de progreso.
 */
public class SplashScreen extends JWindow {

    private JProgressBar progressBar;
    private final Runnable onComplete;

    public SplashScreen(Runnable onComplete) {
        this.onComplete = onComplete;
        initialize();
    }

    private void initialize() {
        setSize(500, 300);
        setLocationRelativeTo(null);

        JPanel contentPane = (JPanel) getContentPane();
        contentPane.setBackground(Theme.COLOR_SECONDARY);
        contentPane.setLayout(new BorderLayout());
        contentPane.setBorder(BorderFactory.createLineBorder(Theme.COLOR_ACCENT_GOLD, 4));

        // Título estilizado
        JLabel lblTitle = new JLabel("CHUPIRULES BARBER SHOP");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.COLOR_ACCENT_GOLD);
        contentPane.add(lblTitle, BorderLayout.CENTER);

        JLabel lblLoading = new JLabel("Cargando sistema...");
        lblLoading.setHorizontalAlignment(SwingConstants.CENTER);
        lblLoading.setFont(new Font("Serif", Font.ITALIC, 14));
        lblLoading.setForeground(Theme.COLOR_TEXT);
        contentPane.add(lblLoading, BorderLayout.NORTH);

        // Barra de progreso dorada
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setForeground(Theme.COLOR_ACCENT_GOLD);
        progressBar.setBackground(Theme.COLOR_PRIMARY);
        progressBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPane.add(progressBar, BorderLayout.SOUTH);
    }

    public void start() {
        this.setVisible(true);
        // Worker para simular carga
        new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(30); // Simula tiempo de carga
                    publish(i);
                }
                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                progressBar.setValue(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                dispose(); // Cerrar splash
                if (onComplete != null) {
                    onComplete.run(); // Ejecutar callback (abrir main)
                }
            }
        }.execute();
    }
}
