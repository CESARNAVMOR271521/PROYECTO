package proyecto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
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

public class ServiciosPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNombre, txtDescripcion, txtPrecio;
    
    private final Color BTN_DEFAULT = new Color(199, 179, 106);
    private final Color TXT_MAIN = new Color(60, 45, 20);

    public ServiciosPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 240, 220));

        JLabel lblTitle = new JLabel("Catálogo de Servicios");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 24));
        lblTitle.setForeground(TXT_MAIN);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        formPanel.setBackground(new Color(245, 240, 220));

        txtNombre = new JTextField();
        txtDescripcion = new JTextField();
        txtPrecio = new JTextField();

        formPanel.add(new JLabel("Nombre:"));
        formPanel.add(txtNombre);
        formPanel.add(new JLabel("Descripción:"));
        formPanel.add(txtDescripcion);
        formPanel.add(new JLabel("Precio:"));
        formPanel.add(txtPrecio);

        String[] columnNames = {"ID", "Nombre", "Descripción", "Precio"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

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

        JPanel southContainer = new JPanel(new BorderLayout());
        southContainer.add(formPanel, BorderLayout.CENTER);
        southContainer.add(btnPanel, BorderLayout.SOUTH);
        add(southContainer, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addServicio());
        btnUpdate.addActionListener(e -> updateServicio());
        btnDelete.addActionListener(e -> deleteServicio());
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
             ResultSet rs = stmt.executeQuery("SELECT * FROM Servicio")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id_servicio"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getDouble("precio")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando datos: " + e.getMessage());
        }
    }

    private void addServicio() {
        try {
            double precio = Double.parseDouble(txtPrecio.getText());
            String sql = "INSERT INTO Servicio(nombre, descripcion, precio) VALUES(?,?,?)";
            try (Connection conn = DatabaseHelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, txtNombre.getText());
                pstmt.setString(2, txtDescripcion.getText());
                pstmt.setDouble(3, precio);
                pstmt.executeUpdate();
                loadData();
                clearForm();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio inválido");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage());
        }
    }

    private void updateServicio() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        int id = (int) tableModel.getValueAt(row, 0);
        
        try {
            double precio = Double.parseDouble(txtPrecio.getText());
            String sql = "UPDATE Servicio SET nombre=?, descripcion=?, precio=? WHERE id_servicio=?";
            try (Connection conn = DatabaseHelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, txtNombre.getText());
                pstmt.setString(2, txtDescripcion.getText());
                pstmt.setDouble(3, precio);
                pstmt.setInt(4, id);
                pstmt.executeUpdate();
                loadData();
                clearForm();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio inválido");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + e.getMessage());
        }
    }

    private void deleteServicio() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        int id = (int) tableModel.getValueAt(row, 0);
        
        if (JOptionPane.showConfirmDialog(this, "¿Seguro de eliminar?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM Servicio WHERE id_servicio=?";
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
        txtDescripcion.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");
        txtPrecio.setText(tableModel.getValueAt(row, 3).toString());
    }

    private void clearForm() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        table.clearSelection();
    }
}
