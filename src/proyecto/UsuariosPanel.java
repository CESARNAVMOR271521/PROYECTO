package proyecto;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

import proyecto.util.Theme;

public class UsuariosPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtUsuario, txtPassword, txtBuscar;
    private JComboBox<String> cbRol;

    public UsuariosPanel() {
        setLayout(new BorderLayout(10, 10));
        Theme.applyTheme(this);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.COLOR_PRIMARY);

        JLabel lblTitle = new JLabel("Administración de Usuarios");
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

        txtUsuario = new JTextField();
        txtPassword = new JTextField();
        cbRol = new JComboBox<>(new String[] { "admin", "barbero", "empleado" });

        addLabel(formPanel, "Usuario:");
        formPanel.add(txtUsuario);
        addLabel(formPanel, "Contraseña:");
        formPanel.add(txtPassword);
        addLabel(formPanel, "Rol:");
        formPanel.add(cbRol);

        String[] columnNames = { "ID", "Usuario", "Rol" };
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

        JButton btnAdd = Theme.createStyledButton("Agregar");
        JButton btnUpdate = Theme.createStyledButton("Actualizar");
        JButton btnDelete = Theme.createStyledButton("Eliminar");
        JButton btnClear = Theme.createStyledButton("Limpiar");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        VoiceButton btnVoice = new VoiceButton();
        btnVoice.setBackground(Theme.COLOR_ACCENT_GOLD);
        btnVoice.setForeground(Theme.COLOR_PRIMARY);
        btnPanel.add(btnVoice);

        JPanel southContainer = new JPanel(new BorderLayout());
        southContainer.add(formPanel, BorderLayout.CENTER);
        southContainer.add(btnPanel, BorderLayout.SOUTH);
        add(southContainer, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addUsuario());
        btnUpdate.addActionListener(e -> updateUsuario());
        btnDelete.addActionListener(e -> deleteUsuario());
        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                loadSelection();
            }
        });

        loadData();

        // Global Focus Tracking
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", e -> {
            java.awt.Component c = (java.awt.Component) e.getNewValue();
            if (c instanceof JTextField) {
                btnVoice.setTargetComponent(c);
            }
        });
    }

    private void addLabel(JPanel panel, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Theme.COLOR_TEXT);
        lbl.setFont(Theme.FONT_BOLD);
        panel.add(lbl);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseHelper.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM Usuario")) {

            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getInt("id_usuario"),
                        rs.getString("nombre_usuario"),
                        rs.getString("rol")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando datos: " + e.getMessage());
        }
    }

    private void addUsuario() {
        String sql = "INSERT INTO Usuario(nombre_usuario, password, rol) VALUES(?,?,?)";
        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, txtUsuario.getText());
            String hashed = proyecto.util.PasswordUtil.hashPassword(txtPassword.getText());
            pstmt.setString(2, hashed);
            pstmt.setString(3, (String) cbRol.getSelectedItem());
            pstmt.executeUpdate();
            loadData();
            clearForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar (Usuario duplicado?): " + e.getMessage());
        }
    }

    private void updateUsuario() {
        int row = table.getSelectedRow();
        if (row == -1)
            return;
        int id = (int) tableModel.getValueAt(row, 0);

        String sql = "UPDATE Usuario SET nombre_usuario=?, password=?, rol=? WHERE id_usuario=?";
        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, txtUsuario.getText());
            String hashed = proyecto.util.PasswordUtil.hashPassword(txtPassword.getText());
            pstmt.setString(2, hashed);
            pstmt.setString(3, (String) cbRol.getSelectedItem());
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
            loadData();
            clearForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + e.getMessage());
        }
    }

    private void deleteUsuario() {
        int row = table.getSelectedRow();
        if (row == -1)
            return;
        int id = (int) tableModel.getValueAt(row, 0);

        if (JOptionPane.showConfirmDialog(this, "¿Seguro de eliminar?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM Usuario WHERE id_usuario=?";
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
        txtUsuario.setText(tableModel.getValueAt(row, 1).toString());
        cbRol.setSelectedItem(tableModel.getValueAt(row, 2).toString());
        // For security, don't load password back into text field
    }

    private void clearForm() {
        txtUsuario.setText("");
        txtPassword.setText("");
        cbRol.setSelectedIndex(0);
        table.clearSelection();
    }
}
