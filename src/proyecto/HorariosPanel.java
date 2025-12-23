package proyecto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextField;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

public class HorariosPanel extends JPanel {

    private JTable table;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtBuscar;

    private final Color BTN_DEFAULT = new Color(199, 179, 106);
    private final Color TXT_MAIN = new Color(60, 45, 20);

    public HorariosPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 240, 220));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 240, 220));

        JLabel lblTitle = new JLabel("Gestión de Horarios y Citas");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 24));
        lblTitle.setForeground(TXT_MAIN);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(new Color(245, 240, 220));
        searchPanel.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(20);
        searchPanel.add(txtBuscar);
        headerPanel.add(searchPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = { "ID", "Fecha", "Hora", "Cliente", "Barbero", "Servicio", "Estado" };
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = txtBuscar.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(new Color(245, 240, 220));

        JButton btnDelete = createButton("Cancelar Cita");
        JButton btnRefresh = createButton("Refrescar");

        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);

        VoiceButton btnVoice = new VoiceButton();
        btnPanel.add(btnVoice);

        add(btnPanel, BorderLayout.SOUTH);

        btnDelete.addActionListener(e -> deleteCita());
        btnRefresh.addActionListener(e -> loadData());

        // Global Focus Tracking
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", e -> {
            java.awt.Component c = (java.awt.Component) e.getNewValue();
            if (c instanceof javax.swing.JTextField) {
                btnVoice.setTargetComponent(c);
            }
        });

        loadData();
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(BTN_DEFAULT);
        btn.setForeground(TXT_MAIN);
        btn.setFocusPainted(false);
        return btn;
    }

    private void loadData() {
        tableModel.setRowCount(0);
        String sql = "SELECT c.id_cita, c.fecha, c.hora, cl.nombre as cliente, b.nombre as barbero, s.nombre as servicio, c.estado "
                +
                "FROM Cita c " +
                "JOIN Cliente cl ON c.id_cliente = cl.id_cliente " +
                "JOIN Barbero b ON c.id_barbero = b.id_barbero " +
                "JOIN Servicio s ON c.id_servicio = s.id_servicio";

        try (Connection conn = DatabaseHelper.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getInt("id_cita"),
                        rs.getString("fecha"),
                        rs.getString("hora"),
                        rs.getString("cliente"),
                        rs.getString("barbero"),
                        rs.getString("servicio"),
                        rs.getString("estado")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando citas: " + e.getMessage());
        }
    }

    private void deleteCita() {
        int row = table.getSelectedRow();
        if (row == -1)
            return;
        int id = (int) tableModel.getValueAt(row, 0);

        if (JOptionPane.showConfirmDialog(this, "¿Cancelar esta cita?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseHelper.connect();
                    PreparedStatement pstmt = conn
                            .prepareStatement("UPDATE Cita SET estado='cancelado' WHERE id_cita=?")) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                loadData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cancelar: " + e.getMessage());
            }
        }
    }
}
