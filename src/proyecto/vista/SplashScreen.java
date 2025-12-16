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
        contentPane.setBackground(new Color(233, 227, 200)); // Mármol oscuro
        contentPane.setLayout(new BorderLayout());
        contentPane.setBorder(BorderFactory.createLineBorder(new Color(140, 112, 60), 4));

        // Título estilizado
        JLabel lblTitle = new JLabel("CHUPIRULES BARBER SHOP");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(new Font("Serif", Font.BOLD, 28));
        lblTitle.setForeground(new Color(60, 45, 20)); // Café oscuro
        contentPane.add(lblTitle, BorderLayout.CENTER);

        JLabel lblLoading = new JLabel("Cargando sistema...");
        lblLoading.setHorizontalAlignment(SwingConstants.CENTER);
        lblLoading.setFont(new Font("Serif", Font.ITALIC, 14));
        lblLoading.setForeground(new Color(100, 80, 50));
        contentPane.add(lblLoading, BorderLayout.NORTH);

        // Barra de progreso dorada
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(242, 213, 107)); // Dorado
        progressBar.setBackground(new Color(60, 45, 20)); // Fondo oscuro
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
