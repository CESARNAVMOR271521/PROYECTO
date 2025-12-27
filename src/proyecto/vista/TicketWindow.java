package proyecto.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TicketWindow extends JDialog {

    public TicketWindow(Window owner, String clientName, DefaultTableModel cartModel, double total, String paymentMethod) {
        super(owner, "Ticket de Venta", ModalityType.APPLICATION_MODAL);
        setSize(400, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        // Header
        JLabel lblHeader = new JLabel("Barbería Chupirules");
        lblHeader.setFont(new Font("Monospaced", Font.BOLD, 20));
        lblHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblDate = new JLabel("Fecha: " + LocalDate.now() + " Hora: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        lblDate.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblDate.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(lblHeader);
        contentPanel.add(Box.createVerticalStrut(5));

        // Logo
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon("src/img/logo.jpg").getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
            JLabel lblLogo = new JLabel(icon);
            lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(lblLogo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(lblDate);
        contentPanel.add(Box.createVerticalStrut(20));

        // Client info
        addLabel(contentPanel, "Cliente: " + clientName);
        addLabel(contentPanel, "Método de Pago: " + paymentMethod);
        contentPanel.add(Box.createVerticalStrut(10));
        addLabel(contentPanel, "--------------------------------");

        // Items
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            String name = (String) cartModel.getValueAt(i, 2);
            int qty = (int) cartModel.getValueAt(i, 4);
            double price = (double) cartModel.getValueAt(i, 3);
            double sub = (double) cartModel.getValueAt(i, 5);
            
            // Format: Qty x Name ... Subtotal
            String line1 = String.format("%dx %s", qty, name);
            String line2 = String.format("%30s", String.format("$%.2f", sub));
            
            addLabel(contentPanel, line1);
            addLabel(contentPanel, line2);
        }
        
        addLabel(contentPanel, "--------------------------------");
        
        // Total
        JLabel lblTotal = new JLabel("TOTAL: $" + String.format("%.2f", total));
        lblTotal.setFont(new Font("Monospaced", Font.BOLD, 18));
        lblTotal.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(lblTotal);
        
        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel();
        JButton btnPrint = new JButton("Imprimir");
        JButton btnClose = new JButton("Cerrar");
        
        btnPrint.addActionListener(e -> JOptionPane.showMessageDialog(this, "Imprimiendo ticket... (Simulación)"));
        btnClose.addActionListener(e -> dispose());
        
        btnPanel.add(btnPrint);
        btnPanel.add(btnClose);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void addLabel(JPanel panel, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lbl);
    }
}
