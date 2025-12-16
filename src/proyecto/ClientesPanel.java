package proyecto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class ClientesPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNombre, txtTelefono, txtCorreo, txtHistorial;
    
    // Theme Colors (Reusing Godhome Theme)
    private final Color BTN_DEFAULT = new Color(199, 179, 106);
    private final Color TXT_MAIN = new Color(60, 45, 20);

    public ClientesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 240, 220));

        // Title
        JLabel lblTitle = new JLabel("Gestión de Clientes");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 24));
        lblTitle.setForeground(TXT_MAIN);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        formPanel.setBackground(new Color(245, 240, 220));

        txtNombre = new JTextField();
        txtTelefono = new JTextField();
        txtCorreo = new JTextField();
        txtHistorial = new JTextField();

        formPanel.add(new JLabel("Nombre:"));
        formPanel.add(txtNombre);
        formPanel.add(new JLabel("Teléfono:"));
        formPanel.add(txtTelefono);
        formPanel.add(new JLabel("Correo:"));
        formPanel.add(txtCorreo);
        formPanel.add(new JLabel("Historial (Notas):"));
        formPanel.add(txtHistorial);

        add(formPanel, BorderLayout.SOUTH); // Place form at bottom initially or wrapped in another panel

        // Table Panel
        String[] columnNames = {"ID", "Nombre", "Teléfono", "Correo", "Historial"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(new Color(245, 240, 220));

        JButton btnAdd = createButton("Agregar");
        JButton btnUpdate = createButton("Actualizar");
        JButton btnDelete = createButton("Eliminar");
        JButton btnClear = createButton("Limpiar");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        // Layout adjustment for Form and Buttons
        JPanel southContainer = new JPanel(new BorderLayout());
        southContainer.add(formPanel, BorderLayout.CENTER);
        southContainer.add(btnPanel, BorderLayout.SOUTH);
        add(southContainer, BorderLayout.SOUTH);

        // Listeners
        btnAdd.addActionListener(e -> addCliente());
        btnUpdate.addActionListener(e -> updateCliente());
        btnDelete.addActionListener(e -> deleteCliente());
        btnClear.addActionListener(e -> clearForm());
        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                loadSelection();
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
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Cliente")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id_cliente"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("correo"),
                    rs.getString("historial")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando datos: " + e.getMessage());
        }
    }

    private void addCliente() {
        String sql = "INSERT INTO Cliente(nombre, telefono, correo, historial) VALUES(?,?,?,?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, txtNombre.getText());
            pstmt.setString(2, txtTelefono.getText());
            pstmt.setString(3, txtCorreo.getText());
            pstmt.setString(4, txtHistorial.getText());
            pstmt.executeUpdate();
            loadData();
            clearForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage());
        }
    }

    private void updateCliente() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        int id = (int) tableModel.getValueAt(row, 0);
        String sql = "UPDATE Cliente SET nombre=?, telefono=?, correo=?, historial=? WHERE id_cliente=?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, txtNombre.getText());
            pstmt.setString(2, txtTelefono.getText());
            pstmt.setString(3, txtCorreo.getText());
            pstmt.setString(4, txtHistorial.getText());
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
            loadData();
            clearForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + e.getMessage());
        }
    }

    private void deleteCliente() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        int id = (int) tableModel.getValueAt(row, 0);
        String sql = "DELETE FROM Cliente WHERE id_cliente=?";

        if (JOptionPane.showConfirmDialog(this, "¿Seguro de eliminar?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseHelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                loadData();
                clearForm();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage());
            }
        }
    }

    private void loadSelection() {
        int row = table.getSelectedRow();
        txtNombre.setText(tableModel.getValueAt(row, 1).toString());
        txtTelefono.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");
        txtCorreo.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
        txtHistorial.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
    }

    private void clearForm() {
        txtNombre.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtHistorial.setText("");
        table.clearSelection();
    }
}
