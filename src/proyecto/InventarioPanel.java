package proyecto;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

import proyecto.util.Theme;

public class InventarioPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbProducto;
    private JTextField txtCantidad, txtMinimo, txtBuscar;
    private ArrayList<Integer> productoIds = new ArrayList<>();

    public InventarioPanel() {
        setLayout(new BorderLayout(10, 10));
        Theme.applyTheme(this);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.COLOR_PRIMARY);

        JLabel lblTitle = new JLabel("Control de Inventario");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.COLOR_ACCENT_GOLD);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Theme.COLOR_PRIMARY);
        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setForeground(Theme.COLOR_TEXT);
        searchPanel.add(lblBuscar);
        
        txtBuscar = new JTextField(20);
        searchPanel.add(txtBuscar);
        headerPanel.add(searchPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        formPanel.setBackground(Theme.COLOR_SECONDARY);

        cbProducto = new JComboBox<>();
        txtCantidad = new JTextField();
        txtMinimo = new JTextField();
        
        // Form Labels and styling
        addLabel(formPanel, "Producto:");
        formPanel.add(cbProducto);
        
        addLabel(formPanel, "Cantidad Actual:");
        formPanel.add(txtCantidad);
        
        addLabel(formPanel, "Stock Mínimo:");
        formPanel.add(txtMinimo);

        String[] columnNames = { "ID", "Producto", "Stock Actual", "Mínimo" };
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        Theme.styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Theme.COLOR_SECONDARY);
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
        btnPanel.setBackground(Theme.COLOR_PRIMARY);

        JButton btnAdd = Theme.createStyledButton("Actualizar Stock");
        JButton btnRefresh = Theme.createStyledButton("Refrescar");

        btnPanel.add(btnAdd);
        btnPanel.add(btnRefresh);

        VoiceButton btnVoice = new VoiceButton();
        btnVoice.setBackground(Theme.COLOR_ACCENT_GOLD);
        btnVoice.setForeground(Theme.COLOR_PRIMARY);
        btnPanel.add(btnVoice);

        JPanel southContainer = new JPanel(new BorderLayout());
        southContainer.add(formPanel, BorderLayout.CENTER);
        southContainer.add(btnPanel, BorderLayout.SOUTH);
        add(southContainer, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> updateStock());
        btnRefresh.addActionListener(e -> {
            loadProductos();
            loadData();
        });

        // Global Focus Tracking
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", e -> {
            java.awt.Component c = (java.awt.Component) e.getNewValue();
            if (c instanceof JTextField) {
                btnVoice.setTargetComponent(c);
            }
        });

        loadProductos();

        loadData();

    }

    private void addLabel(JPanel panel, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Theme.COLOR_TEXT);
        lbl.setFont(Theme.FONT_BOLD);
        panel.add(lbl);
    }

    private void loadProductos() {
        cbProducto.removeAllItems();
        productoIds.clear();
        try (Connection conn = DatabaseHelper.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id_producto, nombre FROM Producto")) {

            while (rs.next()) {
                productoIds.add(rs.getInt("id_producto"));
                cbProducto.addItem(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        String sql = "SELECT i.id_inventario, p.nombre, i.cantidad_actual, i.minimo " +
                "FROM Inventario i " +
                "JOIN Producto p ON i.id_producto = p.id_producto";
        try (Connection conn = DatabaseHelper.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getInt("id_inventario"),
                        rs.getString("nombre"),
                        rs.getInt("cantidad_actual"),
                        rs.getInt("minimo")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando inventario: " + e.getMessage());
        }
    }

    private void updateStock() {
        if (cbProducto.getSelectedIndex() == -1)
            return;

        int idProducto = productoIds.get(cbProducto.getSelectedIndex());

        try {
            int cantidad = Integer.parseInt(txtCantidad.getText());
            int minimo = Integer.parseInt(txtMinimo.getText());

            // Check if entry exists
            boolean exists = false;
            try (Connection conn = DatabaseHelper.connect();
                    PreparedStatement checkStmt = conn
                            .prepareStatement("SELECT 1 FROM Inventario WHERE id_producto=?")) {
                checkStmt.setInt(1, idProducto);
                if (checkStmt.executeQuery().next())
                    exists = true;
            }

            String sql;
            if (exists) {
                sql = "UPDATE Inventario SET cantidad_actual=?, minimo=? WHERE id_producto=?";
            } else {
                sql = "INSERT INTO Inventario(cantidad_actual, minimo, id_producto) VALUES(?,?,?)";
            }

            try (Connection conn = DatabaseHelper.connect();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, cantidad);
                pstmt.setInt(2, minimo);
                pstmt.setInt(3, idProducto);
                pstmt.executeUpdate();
                loadData();
                txtCantidad.setText("");
                txtMinimo.setText("");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valores numéricos inválidos");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + e.getMessage());
        }
    }
}
