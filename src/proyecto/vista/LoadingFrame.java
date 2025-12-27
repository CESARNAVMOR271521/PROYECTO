package proyecto.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoadingFrame extends JFrame {

    private JProgressBar progressBar;
    private JLabel lblStatus;
    private java.util.function.Consumer<LoadingFrame> task;

    public LoadingFrame(java.util.function.Consumer<LoadingFrame> task) {
        this.task = task;

        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 100);
        setLocationRelativeTo(null); // Center on screen

        JPanel contentPane = new JPanel();
        contentPane.setBackground(proyecto.util.Theme.COLOR_PRIMARY);
        contentPane.setBorder(BorderFactory.createLineBorder(proyecto.util.Theme.COLOR_ACCENT_GOLD, 2));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblTitle = new JLabel("Cargando Sistema...");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(proyecto.util.Theme.FONT_TITLE);
        lblTitle.setForeground(proyecto.util.Theme.COLOR_ACCENT_GOLD);
        lblTitle.setBounds(10, 10, 380, 25);
        contentPane.add(lblTitle);

        lblStatus = new JLabel("Iniciando...");
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        lblStatus.setFont(proyecto.util.Theme.FONT_REGULAR);
        lblStatus.setForeground(proyecto.util.Theme.COLOR_TEXT);
        lblStatus.setBounds(10, 40, 380, 15);
        contentPane.add(lblStatus);

        progressBar = new JProgressBar();
        progressBar.setBounds(20, 65, 360, 15);
        progressBar.setStringPainted(true);
        progressBar.setBackground(proyecto.util.Theme.COLOR_SECONDARY);
        progressBar.setForeground(proyecto.util.Theme.COLOR_ACCENT_GOLD);
        contentPane.add(progressBar);
    }

    public void startLoading() {
        setVisible(true);
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Execute the task, passing this frame to report progress
                if (task != null) {
                    task.accept(LoadingFrame.this);
                }
                return null;
            }

            @Override
            protected void done() {
                dispose();
            }
        };
        worker.execute();
    }
    
    public void updateProgress(int percent, String message) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(percent);
            lblStatus.setText(message);
        });
        try {
            // Small delay to make the text readable if loading is too fast
            Thread.sleep(50); 
        } catch (InterruptedException e) {}
    }
}
