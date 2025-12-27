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

public class CitasPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbCliente, cbBarbero, cbServicio;
    private JTextField txtFecha, txtHora, txtBuscar;
    private ArrayList<Integer> clienteIds = new ArrayList<>();
    private ArrayList<Integer> barberoIds = new ArrayList<>();
    private ArrayList<Integer> servicioIds = new ArrayList<>();

    public CitasPanel() {
        setLayout(new BorderLayout(10, 10));
        Theme.applyTheme(this);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.COLOR_PRIMARY);

        JLabel lblTitle = new JLabel("Agenda y Citas");
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

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        formPanel.setBackground(Theme.COLOR_SECONDARY);

        cbCliente = new JComboBox<>();
        cbBarbero = new JComboBox<>();
        cbServicio = new JComboBox<>();
        txtFecha = new JTextField("2025-01-01"); // Placeholder
        txtHora = new JTextField("10:00");

        addLabel(formPanel, "Cliente:");
        formPanel.add(cbCliente);
        addLabel(formPanel, "Barbero:");
        formPanel.add(cbBarbero);
        addLabel(formPanel, "Servicio:");
        formPanel.add(cbServicio);
        addLabel(formPanel, "Fecha (YYYY-MM-DD):");
        formPanel.add(txtFecha);
        addLabel(formPanel, "Hora (HH:MM):");
        formPanel.add(txtHora);

        String[] columnNames = { "ID", "Fecha", "Hora", "Cliente", "Barbero", "Servicio", "Estado" };
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

        JButton btnAdd = Theme.createStyledButton("Agendar Cita");
        JButton btnDelete = Theme.createStyledButton("Cancelar Cita");
        JButton btnRefresh = Theme.createStyledButton("Refrescar");

        VoiceButton btnVoice = new VoiceButton();
        btnVoice.setBackground(Theme.COLOR_ACCENT_GOLD);
        btnVoice.setForeground(Theme.COLOR_PRIMARY);

        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);
        btnPanel.add(btnVoice);

        JPanel southContainer = new JPanel(new BorderLayout());
        southContainer.add(formPanel, BorderLayout.CENTER);
        southContainer.add(btnPanel, BorderLayout.SOUTH);
        add(southContainer, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addCita());
        btnDelete.addActionListener(e -> deleteCita());
        btnRefresh.addActionListener(e -> loadData());

        // Global Focus Tracking
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", e -> {
            java.awt.Component c = (java.awt.Component) e.getNewValue();
            if (c instanceof JTextField) {
                btnVoice.setTargetComponent(c);
            }
        });

        loadComboBoxes();
        loadData();
    }

    private void addLabel(JPanel panel, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Theme.COLOR_TEXT);
        lbl.setFont(Theme.FONT_BOLD);
        panel.add(lbl);
    }

    private void loadComboBoxes() {
        cbCliente.removeAllItems();
        clienteIds.clear();
        cbBarbero.removeAllItems();
        barberoIds.clear();
        cbServicio.removeAllItems();
        servicioIds.clear();

        try (Connection conn = DatabaseHelper.connect();
                Statement stmt = conn.createStatement()) {

            // Clientes
            ResultSet rs = stmt.executeQuery("SELECT id_cliente, nombre FROM Cliente");
            while (rs.next()) {
                clienteIds.add(rs.getInt("id_cliente"));
                cbCliente.addItem(rs.getString("nombre"));
            }
            rs.close();

            // Barberos
            rs = stmt.executeQuery("SELECT id_barbero, nombre FROM Barbero WHERE activo=1");
            while (rs.next()) {
                barberoIds.add(rs.getInt("id_barbero"));
                cbBarbero.addItem(rs.getString("nombre"));
            }
            rs.close();

            // Servicios
            rs = stmt.executeQuery("SELECT id_servicio, nombre FROM Servicio");
            while (rs.next()) {
                servicioIds.add(rs.getInt("id_servicio"));
                cbServicio.addItem(rs.getString("nombre"));
            }
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    private void addCita() {
        if (cbCliente.getSelectedIndex() == -1 || cbBarbero.getSelectedIndex() == -1
                || cbServicio.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione todos los campos");
            return;
        }

        int idCliente = clienteIds.get(cbCliente.getSelectedIndex());
        int idBarbero = barberoIds.get(cbBarbero.getSelectedIndex());
        int idServicio = servicioIds.get(cbServicio.getSelectedIndex());

        String sql = "INSERT INTO Cita(fecha, hora, id_cliente, id_barbero, id_servicio, estado) VALUES(?,?,?,?,?,?)";
        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, txtFecha.getText());
            pstmt.setString(2, txtHora.getText());
            pstmt.setInt(3, idCliente);
            pstmt.setInt(4, idBarbero);
            pstmt.setInt(5, idServicio);
            pstmt.setString(6, "pendiente");
            pstmt.executeUpdate();
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agendar: " + e.getMessage());
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
