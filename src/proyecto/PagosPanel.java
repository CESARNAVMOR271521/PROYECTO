package proyecto;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;

import proyecto.util.Theme;

public class PagosPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cbFiltroMetodo;

    public PagosPanel() {
        setLayout(new BorderLayout(10, 10));
        Theme.applyTheme(this);

        // Title
        JLabel lblTitle = new JLabel("Gestión de Pagos");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.COLOR_ACCENT_GOLD);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Theme.COLOR_SECONDARY);
        filterPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Theme.COLOR_ACCENT_GOLD), "Filtros", 
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
            javax.swing.border.TitledBorder.DEFAULT_POSITION, 
            Theme.FONT_REGULAR, Theme.COLOR_ACCENT_GOLD));

        JLabel lblClient = new JLabel("Buscar Cliente:");
        lblClient.setForeground(Theme.COLOR_TEXT);
        filterPanel.add(lblClient);
        txtSearch = new JTextField(15);
        filterPanel.add(txtSearch);

        JLabel lblMetodo = new JLabel("Método de Pago:");
        lblMetodo.setForeground(Theme.COLOR_TEXT);
        filterPanel.add(lblMetodo);
        cbFiltroMetodo = new JComboBox<>(new String[] { "Todos", "Efectivo", "Tarjeta" });
        filterPanel.add(cbFiltroMetodo);

        JButton btnRefresh = Theme.createStyledButton("Refrescar");
        filterPanel.add(btnRefresh);

        // Table
        String[] columnNames = { "ID Pago", "ID Venta", "Fecha", "Cliente", "Método", "Monto", "Estado" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);

        // Hide ID Pago column (index 0)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        Theme.styleTable(table);

        // Configure Sorter
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Theme.COLOR_SECONDARY);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Theme.COLOR_PRIMARY);

        JButton btnAnular = Theme.createStyledButton("Anular Pago");
        actionPanel.add(btnAnular);

        VoiceButton btnVoice = new VoiceButton();
        btnVoice.setBackground(Theme.COLOR_ACCENT_GOLD);
        btnVoice.setForeground(Theme.COLOR_PRIMARY);
        actionPanel.add(btnVoice);

        add(actionPanel, BorderLayout.SOUTH);

        // Listeners
        btnRefresh.addActionListener(e -> loadData());

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterData(sorter);
            }
        });

        cbFiltroMetodo.addActionListener(e -> filterData(sorter));

        btnAnular.addActionListener(e -> anularPago());

        // Global Focus Tracking
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", e -> {
            java.awt.Component c = (java.awt.Component) e.getNewValue();
            if (c instanceof JTextField) {
                btnVoice.setTargetComponent(c);
            }
        });

        // Initial Load
        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        String sql = "SELECT p.id_pago, p.id_venta, v.fecha, c.nombre as cliente, p.forma_pago, p.monto, p.estado " +
                "FROM Pago p " +
                "JOIN Venta v ON p.id_venta = v.id_venta " +
                "LEFT JOIN Cliente c ON v.id_cliente = c.id_cliente " +
                "ORDER BY p.id_pago DESC";

        try (Connection conn = DatabaseHelper.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String cliente = rs.getString("cliente");
                if (cliente == null)
                    cliente = "Cliente Casual";

                tableModel.addRow(new Object[] {
                        rs.getInt("id_pago"),
                        rs.getInt("id_venta"),
                        rs.getString("fecha"),
                        cliente,
                        rs.getString("forma_pago"),
                        String.format("%.2f", rs.getDouble("monto")),
                        rs.getString("estado")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando pagos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filterData(TableRowSorter<DefaultTableModel> sorter) {
        String text = txtSearch.getText();
        String metodo = (String) cbFiltroMetodo.getSelectedItem();

        java.util.List<RowFilter<Object, Object>> filters = new java.util.ArrayList<>();

        // Filter by text (Client Name - Index 3)
        if (text.trim().length() > 0) {
            filters.add(RowFilter.regexFilter("(?i)" + text, 3));
        }

        // Filter by Method (Index 4)
        if (!"Todos".equals(metodo)) {
            filters.add(RowFilter.regexFilter(metodo, 4));
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private void anularPago() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un pago para anular.");
            return;
        }

        // Convert index if sorted
        int modelRow = table.convertRowIndexToModel(selectedRow);
        int idPago = (int) tableModel.getValueAt(modelRow, 0);
        String estadoActual = (String) tableModel.getValueAt(modelRow, 6);

        if ("Anulado".equalsIgnoreCase(estadoActual)) {
            JOptionPane.showMessageDialog(this, "El pago ya está anulado.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de anular este pago?\nEsto no afectará el inventario automáticamente.",
                "Confirmar Anulación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "UPDATE Pago SET estado = 'Anulado' WHERE id_pago = ?";
            try (Connection conn = DatabaseHelper.connect();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, idPago);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Pago anulado correctamente.");
                loadData();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al anular pago: " + e.getMessage());
            }
        }
    }
}
