package proyecto.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import proyecto.dao.ClienteDAO;
import proyecto.modelo.Cliente;
import proyecto.util.Theme;

public class ClientePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private ClienteDAO clienteDAO;

    public ClientePanel() {
        setLayout(new BorderLayout());
        clienteDAO = new ClienteDAO();
        Theme.applyTheme(this);
        
        // Toolbar
        JPanel toolbar = new JPanel();
        toolbar.setBackground(Theme.COLOR_PRIMARY);

        JButton btnAgregar = Theme.createStyledButton("Agregar");
        JButton btnRecargar = Theme.createStyledButton("Recargar");
        
        toolbar.add(btnAgregar);
        toolbar.add(btnRecargar);
        add(toolbar, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"ID", "Nombre", "Teléfono", "Correo", "Historial", "Fecha Registro"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        
        Theme.styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Theme.COLOR_SECONDARY);
        add(scrollPane, BorderLayout.CENTER);

        // Actions
        btnRecargar.addActionListener(e -> cargarClientes());
        btnAgregar.addActionListener(e -> agregarCliente());

        cargarClientes();
    }

    private void cargarClientes() {
        model.setRowCount(0);
        List<Cliente> clientes = clienteDAO.listar();
        for (Cliente c : clientes) {
            model.addRow(new Object[]{
                c.getIdCliente(),
                c.getNombre(),
                c.getTelefono(),
                c.getCorreo(),
                c.getHistorial(),
                c.getFechaRegistro()
            });
        }
    }

    private void agregarCliente() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre del cliente:");
        if (nombre != null && !nombre.trim().isEmpty()) {
            Cliente c = new Cliente();
            c.setNombre(nombre);
            c.setTelefono("000-000"); // Dummy data for quick test
            c.setCorreo("test@example.com");
            c.setHistorial("Nuevo cliente");
            if (clienteDAO.insertar(c)) {
                JOptionPane.showMessageDialog(this, "Cliente agregado!");
                cargarClientes();
            } else {
                JOptionPane.showMessageDialog(this, "Error al agregar cliente.");
            }
        }
    }
}
