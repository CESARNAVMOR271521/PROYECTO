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
        setSize(350, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setBackground(Color.WHITE);

        // --- HEADER ---
        addCenteredLabel(contentPanel, "BARBERÍA CHUPIRULES", new Font("Monospaced", Font.BOLD, 16));
        addCenteredLabel(contentPanel, "Calle Falsa 123", new Font("Monospaced", Font.PLAIN, 10));
        addCenteredLabel(contentPanel, "Tel: 555-123-4567", new Font("Monospaced", Font.PLAIN, 10));
        contentPanel.add(Box.createVerticalStrut(10));
        
        // Logo (Optional)
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon("src/img/logo.jpg").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
            JLabel lblLogo = new JLabel(icon);
            lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(lblLogo);
        } catch (Exception e) {}
        contentPanel.add(Box.createVerticalStrut(10));

        // --- INFO ---
        addSeparator(contentPanel);
        addLeftLabel(contentPanel, "Fecha: " + LocalDate.now() + "  Hora: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        addLeftLabel(contentPanel, "Cliente: " + clientName);
        addLeftLabel(contentPanel, "Pago: " + paymentMethod);
        addSeparator(contentPanel);

        // --- ITEMS ---
        // Header Row
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setBackground(Color.WHITE);
        headerRow.add(new JLabel("Cant  Desc"), BorderLayout.WEST);
        headerRow.add(new JLabel("Importe"), BorderLayout.EAST);
        // Force font
        for(Component c : headerRow.getComponents()) c.setFont(new Font("Monospaced", Font.BOLD, 11));
        headerRow.setMaximumSize(new Dimension(350, 20));
        contentPanel.add(headerRow);
        
        addSeparator(contentPanel);

        for (int i = 0; i < cartModel.getRowCount(); i++) {
            String name = (String) cartModel.getValueAt(i, 2);
            // Truncate name if too long
            if (name.length() > 20) name = name.substring(0, 20) + "..";
            
            int qty = (int) cartModel.getValueAt(i, 4);
            double sub = (double) cartModel.getValueAt(i, 5);
            
            JPanel itemRow = new JPanel(new BorderLayout());
            itemRow.setBackground(Color.WHITE);
            
            JLabel lblDesc = new JLabel(qty + " x " + name);
            lblDesc.setFont(new Font("Monospaced", Font.PLAIN, 11));
            
            JLabel lblPrice = new JLabel(String.format("$%.2f", sub));
            lblPrice.setFont(new Font("Monospaced", Font.PLAIN, 11));
            
            itemRow.add(lblDesc, BorderLayout.WEST);
            itemRow.add(lblPrice, BorderLayout.EAST);
            itemRow.setMaximumSize(new Dimension(350, 20));
            contentPanel.add(itemRow);
        }
        
        contentPanel.add(Box.createVerticalStrut(10));
        addSeparator(contentPanel);
        
        // --- TOTAL ---
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(Color.WHITE);
        JLabel lblTotalText = new JLabel("TOTAL A PAGAR:");
        lblTotalText.setFont(new Font("Monospaced", Font.BOLD, 14));
        JLabel lblTotalVal = new JLabel("$" + String.format("%.2f", total));
        lblTotalVal.setFont(new Font("Monospaced", Font.BOLD, 18));
        
        totalPanel.add(lblTotalText, BorderLayout.WEST);
        totalPanel.add(lblTotalVal, BorderLayout.EAST);
        totalPanel.setMaximumSize(new Dimension(350, 30));
        contentPanel.add(totalPanel);
        
        addSeparator(contentPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        
        // --- FOOTER ---
        addCenteredLabel(contentPanel, "¡Vuelva Pronto!", new Font("Monospaced", Font.ITALIC, 12));
        addCenteredLabel(contentPanel, "*** GRACIAS ***", new Font("Monospaced", Font.BOLD, 12));
        
        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(240, 240, 240));
        JButton btnPrint = new JButton("Imprimir");
        btnPrint.setForeground(Color.BLACK);
        JButton btnClose = new JButton("Cerrar");
        btnClose.setForeground(Color.BLACK);
        
        btnPrint.addActionListener(e -> JOptionPane.showMessageDialog(this, "Enviando a impresora térmica..."));
        btnClose.addActionListener(e -> dispose());
        
        btnPanel.add(btnPrint);
        btnPanel.add(btnClose);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void addCenteredLabel(JPanel panel, String text, Font font) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lbl);
    }
    
    private void addLeftLabel(JPanel panel, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Wrapper to force left alignment in BoxLayout if needed, but Component.LEFT_ALIGNMENT usually works if others are also left.
        // However, mixing CENTER and LEFT can be tricky. Let's wrap in a panel.
        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrap.setBackground(Color.WHITE);
        wrap.add(lbl);
        wrap.setMaximumSize(new Dimension(350, 20));
        panel.add(wrap);
    }
    
    private void addSeparator(JPanel panel) {
        JLabel lbl = new JLabel("================================");
        lbl.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lbl);
    }
}
