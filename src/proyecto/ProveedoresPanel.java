package proyecto;

import proyecto.dao.CompraProveedorDAO;
import proyecto.dao.DetalleCompraDAO;
import proyecto.dao.InventarioDAO;
import proyecto.dao.ProductoDAO;
import proyecto.dao.ProveedorDAO;
import proyecto.modelo.CompraProveedor;
import proyecto.modelo.Inventario;
import proyecto.modelo.Producto;
import proyecto.modelo.Proveedor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;

public class ProveedoresPanel extends JPanel {

    // 🎨 ESTILO “SALÓN DE LOS DIOSES” – Copied for consistency
    private final Color BG_MARBLE = new Color(233, 227, 200);
    private final Color BG_GOLD_SOFT = new Color(209, 184, 108);
    private final Color BG_PANEL = new Color(245, 240, 220);
    private final Color TXT_MAIN = new Color(60, 45, 20);
    private final Color BORDER_GOD = new Color(242, 213, 107);

    private ProveedorDAO proveedorDAO;
    private ProductoDAO productoDAO;
    private InventarioDAO inventarioDAO;
    private CompraProveedorDAO compraDAO;

    private JTable tableProveedores;
    private DefaultTableModel modelProveedores;

    private JLabel lblNombre, lblTelefono, lblCorreo;

    private JTable tableHistorial;
    private DefaultTableModel modelHistorial;

    private JTable tableInventario;
    private DefaultTableModel modelInventario;

    public ProveedoresPanel() {
        proveedorDAO = new ProveedorDAO();
        productoDAO = new ProductoDAO();
        inventarioDAO = new InventarioDAO();
        compraDAO = new CompraProveedorDAO();

        setLayout(new BorderLayout());
        setBackground(BG_PANEL);

        // -- SPLIT PANE: Left (List) vs Right (Details) --
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setBackground(BG_PANEL);
        add(splitPane, BorderLayout.CENTER);

        // == LEFT SIDE: Supplier List ==
        JPanel panelList = new JPanel(new BorderLayout());
        panelList.setBackground(BG_PANEL);
        panelList.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_GOD, 2), "Proveedores Registrados"));

        modelProveedores = new DefaultTableModel(new Object[] { "ID", "Nombre" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableProveedores = new JTable(modelProveedores);

        // Hide ID (0)
        tableProveedores.getColumnModel().getColumn(0).setMinWidth(0);
        tableProveedores.getColumnModel().getColumn(0).setMaxWidth(0);
        tableProveedores.getColumnModel().getColumn(0).setWidth(0);

        tableProveedores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableProveedores.setBackground(Color.WHITE);
        panelList.add(new JScrollPane(tableProveedores), BorderLayout.CENTER);

        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(BG_PANEL);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        searchPanel.add(new JLabel("Buscar: "), BorderLayout.WEST);
        JTextField txtBuscar = new JTextField();
        searchPanel.add(txtBuscar, BorderLayout.CENTER);
        panelList.add(searchPanel, BorderLayout.NORTH);

        // Sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelProveedores);
        tableProveedores.setRowSorter(sorter);

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

        // Voice Button Panel
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(BG_PANEL);
        VoiceButton btnVoice = new VoiceButton();
        btnPanel.add(btnVoice);
        panelList.add(btnPanel, BorderLayout.SOUTH);

        // Click listener
        tableProveedores.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableProveedores.getSelectedRow();
                if (row != -1) {
                    int id = (int) modelProveedores.getValueAt(row, 0);
                    cargarDetalles(id);
                }
            }
        });

        splitPane.setLeftComponent(panelList);

        // == RIGHT SIDE: Details (Tabs) ==
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Serif", Font.BOLD, 14));
        tabbedPane.setBackground(BG_GOLD_SOFT);

        // Tab 1: General Info
        JPanel pnlInfo = new JPanel(new GridLayout(4, 2, 10, 10));
        pnlInfo.setBackground(BG_PANEL);
        pnlInfo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lblNombre = new JLabel("-");
        lblTelefono = new JLabel("-");
        lblCorreo = new JLabel("-");

        addInfoRow(pnlInfo, "Nombre:", lblNombre);
        addInfoRow(pnlInfo, "Teléfono:", lblTelefono);
        addInfoRow(pnlInfo, "Correo:", lblCorreo);

        tabbedPane.addTab("Información General", pnlInfo);

        // Tab 2: Historial (Entregas/Compras)
        JPanel pnlHistorial = new JPanel(new BorderLayout());
        modelHistorial = new DefaultTableModel(new Object[] { "ID Compra", "Fecha", "Total" }, 0);
        tableHistorial = new JTable(modelHistorial);

        // Hide ID Compra (0)
        tableHistorial.getColumnModel().getColumn(0).setMinWidth(0);
        tableHistorial.getColumnModel().getColumn(0).setMaxWidth(0);
        tableHistorial.getColumnModel().getColumn(0).setWidth(0);

        pnlHistorial.add(new JScrollPane(tableHistorial), BorderLayout.CENTER);
        tabbedPane.addTab("Historial de Entregas", pnlHistorial);

        // Tab 3: Inventario (Productos de este proveedor)
        JPanel pnlInventario = new JPanel(new BorderLayout());
        modelInventario = new DefaultTableModel(
                new Object[] { "Producto", "Precio Compra", "Precio Venta", "Stock Actual", "Minimo" }, 0);
        tableInventario = new JTable(modelInventario);
        pnlInventario.add(new JScrollPane(tableInventario), BorderLayout.CENTER);
        tabbedPane.addTab("Inventario por Proveedor", pnlInventario);

        splitPane.setRightComponent(tabbedPane);

        cargarProveedores();

        // Global Focus Tracking (For consistency, though no fields here currently)
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", e -> {
            java.awt.Component c = (java.awt.Component) e.getNewValue();
            if (c instanceof javax.swing.JTextField) {
                btnVoice.setTargetComponent(c);
            }
        });
    }

    private void addInfoRow(JPanel p, String label, JLabel value) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Serif", Font.BOLD, 14));
        l.setForeground(TXT_MAIN);
        value.setFont(new Font("SansSerif", Font.PLAIN, 14));
        value.setForeground(Color.BLACK);
        p.add(l);
        p.add(value);
    }

    private void cargarProveedores() {
        modelProveedores.setRowCount(0);
        List<Proveedor> lista = proveedorDAO.listar();
        for (Proveedor p : lista) {
            modelProveedores.addRow(new Object[] { p.getIdProveedor(), p.getNombre() });
        }
    }

    private void cargarDetalles(int idProveedor) {
        // 1. Info General
        List<Proveedor> lista = proveedorDAO.listar();
        Proveedor seleccionado = null;
        for (Proveedor p : lista) {
            if (p.getIdProveedor() == idProveedor) {
                seleccionado = p;
                break;
            }
        }

        if (seleccionado != null) {
            lblNombre.setText(seleccionado.getNombre());
            lblTelefono.setText(seleccionado.getTelefono());
            lblCorreo.setText(seleccionado.getCorreo());
        }

        // 2. Historial
        modelHistorial.setRowCount(0);
        List<CompraProveedor> compras = compraDAO.listarPorProveedor(idProveedor);
        for (CompraProveedor c : compras) {
            modelHistorial.addRow(new Object[] {
                    c.getIdCompra(),
                    c.getFecha(),
                    String.format("$%.2f", c.getTotal())
            });
        }

        // 3. Inventario (Productos)
        modelInventario.setRowCount(0);
        List<Producto> productos = productoDAO.listarPorProveedor(idProveedor);
        for (Producto prod : productos) {
            Inventario inv = inventarioDAO.obtenerPorProducto(prod.getIdProducto());
            int stock = (inv != null) ? inv.getStock() : 0;
            int min = (inv != null) ? inv.getStockMinimo() : 0;

            modelInventario.addRow(new Object[] {
                    prod.getNombre(),
                    String.format("$%.2f", prod.getPrecioCompra()),
                    String.format("$%.2f", prod.getPrecioVenta()),
                    stock,
                    min
            });
        }
    }
}
