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
import proyecto.util.ModernIcon.IconType;

public class CitasPanel extends JPanel implements VoiceAware {

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
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
        
        txtFecha = new JTextField(java.time.LocalDate.now().format(dateFormatter));
        txtHora = new JTextField(java.time.LocalTime.now().format(timeFormatter));

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

        JButton btnAdd = Theme.createStyledButton("Agendar Cita", IconType.ADD);
        JButton btnDelete = Theme.createStyledButton("Cancelar Cita", IconType.CANCEL);
        JButton btnRefresh = Theme.createStyledButton("Refrescar", IconType.REFRESH);

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
        
        // Auto-refresh Date/Time when panel is CONSTANTLY shown
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
                java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
                txtFecha.setText(java.time.LocalDate.now().format(dateFormatter));
                txtHora.setText(java.time.LocalTime.now().format(timeFormatter));
                loadComboBoxes(); 
                loadData();
                System.out.println("CitasPanel: Refreshed data on show.");
            }
        });
        
        Theme.bindActionKeys(this, btnAdd, btnDelete, btnRefresh, null);
        
        // Add Selection Listener
        table.getSelectionModel().addListSelectionListener(e -> {
            boolean isAdjusting = e.getValueIsAdjusting(); 
            if (!isAdjusting) {
                loadSelection();
            }
        });
    }

    private void loadSelection() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        
        int modelRow = table.convertRowIndexToModel(row);
        
        // Columns: 0=ID, 1=Datum, 2=Hora, 3=Cliente, 4=Barbero, 5=Servicio, 6=Estado
        try {
            Object fechaObj = tableModel.getValueAt(modelRow, 1);
            Object horaObj = tableModel.getValueAt(modelRow, 2);
            Object clienteObj = tableModel.getValueAt(modelRow, 3);
            Object barberoObj = tableModel.getValueAt(modelRow, 4);
            Object servicioObj = tableModel.getValueAt(modelRow, 5);

            if (fechaObj != null) txtFecha.setText(fechaObj.toString());
            if (horaObj != null) txtHora.setText(horaObj.toString());
            
            if (clienteObj != null) selectInCombo(cbCliente, clienteObj.toString());
            if (barberoObj != null) selectInCombo(cbBarbero, barberoObj.toString());
            if (servicioObj != null) selectInCombo(cbServicio, servicioObj.toString());
            
        } catch (Exception ex) {
            System.err.println("Error loading selection: " + ex.getMessage());
        }
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
    @Override
    public void handleVoiceCommand(String command, String args) {
        String argUpper = (args != null) ? args.toUpperCase() : "";

        switch (command) {
            case "AGENDAR":
            case "CREATE":
                // If args provided (e.g. "Agendar Juan"), try to set client first
                if (!argUpper.isEmpty()) { 
                     setField("CLIENTE", argUpper);
                }
                addCita();
                break;
            case "DELETE":
            case "CANCEL":
                deleteCita();
                break;
            case "CLEAR":
                // Reset combos to -1? Or defaults?
                cbCliente.setSelectedIndex(-1);
                cbBarbero.setSelectedIndex(-1);
                cbServicio.setSelectedIndex(-1);
                // Reset date/time to now?
                java.time.format.DateTimeFormatter dF = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
                java.time.format.DateTimeFormatter tF = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
                txtFecha.setText(java.time.LocalDate.now().format(dF));
                txtHora.setText(java.time.LocalTime.now().format(tF));
                break;
                
            case "SET_FIELD":
                String[] parts = args.split(" ", 2);
                if (parts.length < 2) return;
                String field = parts[0].toUpperCase();
                String val = parts[1];
                setField(field, val);
                break;
                
            case "SELECT":
            case "SELECCIONAR":
            case "BUSCAR": 
                if (args == null || args.isEmpty()) return;
                String query = args.toUpperCase();
                for (int i = 0; i < table.getRowCount(); i++) {
                    // Check Cliente (3) or Barbero (4)
                     String client = table.getValueAt(i, 3).toString().toUpperCase();
                     String barber = table.getValueAt(i, 4).toString().toUpperCase();
                     if (client.contains(query) || barber.contains(query)) {
                         table.setRowSelectionInterval(i, i);
                         table.scrollRectToVisible(table.getCellRect(i, 0, true));
                         // loadSelection is handled by listener now
                         break;
                     }
                }
                break;
        }
    }

    private void setField(String field, String val) {
        val = val.toUpperCase().trim();
        switch (field) {
            case "CLIENTE":
                selectInCombo(cbCliente, val);
                break;
            case "BARBERO":
                selectInCombo(cbBarbero, val);
                break;
            case "SERVICIO":
                selectInCombo(cbServicio, val);
                break;
            case "FECHA":
                // formats as 2023-01-01
                txtFecha.setText(val); 
                break;
            case "HORA":
                txtHora.setText(val.replace(" PM", "").replace(" AM", "").trim()); 
                break;
        }
    }

    private void selectInCombo(JComboBox<String> cb, String search) {
        for (int i = 0; i < cb.getItemCount(); i++) {
            String item = cb.getItemAt(i).toUpperCase();
            // Match strict or first word (e.g. "JUAN" matches "JUAN PEREZ")
            if (item.contains(search) || (item.length() > 3 && search.contains(item.split(" ")[0]))) {
                cb.setSelectedIndex(i);
                return;
            }
        }
    }
}
