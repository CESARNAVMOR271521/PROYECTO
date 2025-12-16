package proyecto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class HistorialPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    // Theme Colors
    private final Color BTN_DEFAULT = new Color(199, 179, 106);
    private final Color TXT_MAIN = new Color(60, 45, 20);

    public HistorialPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 240, 220));

        JLabel lblTitle = new JLabel("Historial de Ventas");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 24));
        lblTitle.setForeground(TXT_MAIN);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        String[] columnNames = { "ID Venta", "Fecha", "Cliente", "Total", "Tipo" };
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        // Hide ID Venta column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton btnRefresh = new JButton("Actualizar");
        btnRefresh.setBackground(BTN_DEFAULT);
        btnRefresh.setForeground(TXT_MAIN);
        btnRefresh.addActionListener(e -> loadData());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(245, 240, 220));
        bottomPanel.add(btnRefresh);

        VoiceButton btnVoice = new VoiceButton();
        bottomPanel.add(btnVoice);

        add(bottomPanel, BorderLayout.SOUTH);

        // Global Focus Tracking (Even if no fields, for consistency)
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", e -> {
            java.awt.Component c = (java.awt.Component) e.getNewValue();
            if (c instanceof JTextField) {
                btnVoice.setTargetComponent(c);
            }
        });

        loadData();
    }

    public void loadData() {
        tableModel.setRowCount(0);
        String sql = "SELECT v.id_venta, v.fecha, c.nombre, v.total, v.tipo " +
                "FROM Venta v " +
                "LEFT JOIN Cliente c ON v.id_cliente = c.id_cliente " +
                "ORDER BY v.id_venta DESC";

        try (Connection conn = DatabaseHelper.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getInt("id_venta"),
                        rs.getString("fecha"),
                        rs.getString("nombre") != null ? rs.getString("nombre") : "Cliente Casual",
                        rs.getDouble("total"),
                        rs.getString("tipo")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando historial: " + e.getMessage());
        }
    }
}
