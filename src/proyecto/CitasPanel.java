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

public class CitasPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbCliente, cbBarbero, cbServicio;
    private JTextField txtFecha, txtHora;
    private ArrayList<Integer> clienteIds = new ArrayList<>();
    private ArrayList<Integer> barberoIds = new ArrayList<>();
    private ArrayList<Integer> servicioIds = new ArrayList<>();

    private final Color BTN_DEFAULT = new Color(199, 179, 106);
    private final Color TXT_MAIN = new Color(60, 45, 20);

    public CitasPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 240, 220));

        JLabel lblTitle = new JLabel("Agenda y Citas");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 24));
        lblTitle.setForeground(TXT_MAIN);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        formPanel.setBackground(new Color(245, 240, 220));

        cbCliente = new JComboBox<>();
        cbBarbero = new JComboBox<>();
        cbServicio = new JComboBox<>();
        txtFecha = new JTextField("2025-01-01"); // Placeholder
        txtHora = new JTextField("10:00");

        formPanel.add(new JLabel("Cliente:"));
        formPanel.add(cbCliente);
        formPanel.add(new JLabel("Barbero:"));
        formPanel.add(cbBarbero);
        formPanel.add(new JLabel("Servicio:"));
        formPanel.add(cbServicio);
        formPanel.add(new JLabel("Fecha (YYYY-MM-DD):"));
        formPanel.add(txtFecha);
        formPanel.add(new JLabel("Hora (HH:MM):"));
        formPanel.add(txtHora);

        String[] columnNames = { "ID", "Fecha", "Hora", "Cliente", "Barbero", "Servicio", "Estado" };
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

        JButton btnAdd = createButton("Agendar Cita");
        JButton btnDelete = createButton("Cancelar Cita");
        JButton btnRefresh = createButton("Refrescar");

        VoiceButton btnVoice = new VoiceButton();

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

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(BTN_DEFAULT);
        btn.setForeground(TXT_MAIN);
        btn.setFocusPainted(false);
        return btn;
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
