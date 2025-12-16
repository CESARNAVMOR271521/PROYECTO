package proyecto;

import proyecto.dao.CompraProveedorDAO;
import proyecto.dao.DetalleCompraDAO;
import proyecto.dao.InventarioDAO;
import proyecto.dao.ProductoDAO;
import proyecto.dao.ProveedorDAO;
import proyecto.modelo.CompraProveedor;
import proyecto.modelo.DetalleCompra;
import proyecto.modelo.Producto;
import proyecto.modelo.Proveedor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ComprasPanel extends JPanel {

    // 🎨 ESTILOS "SALÓN DE LOS DIOSES"
    private final Color BG_PANEL = new Color(245, 240, 220);
    private final Color BTN_DEFAULT = new Color(199, 179, 106);
    private final Color TXT_MAIN = new Color(60, 45, 20);

    private CompraProveedorDAO compraDAO;
    private DetalleCompraDAO detalleDAO;
    private InventarioDAO inventarioDAO;
    private ProveedorDAO proveedorDAO;
    private ProductoDAO productoDAO;

    private JComboBox<String> cbProveedor;
    private JComboBox<String> cbProducto;
    private JTextField txtCantidad;
    private JTextField txtCosto; // Costo por unidad al momento de la compra
    private JTextField txtTotal;

    private JTable cartTable;
    private DefaultTableModel cartModel;

    private List<Proveedor> listaProveedores;
    private List<Producto> listaProductos;

    public ComprasPanel() {
        compraDAO = new CompraProveedorDAO();
        detalleDAO = new DetalleCompraDAO();
        inventarioDAO = new InventarioDAO();
        proveedorDAO = new ProveedorDAO();
        productoDAO = new ProductoDAO();

        setLayout(new BorderLayout(10, 10));
        setBackground(BG_PANEL);

        // Header
        JLabel lblTitle = new JLabel("Registro de Compras a Proveedores");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 24));
        lblTitle.setForeground(TXT_MAIN);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        // Selection Panel
        JPanel selectionPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Agregar Producto"));
        selectionPanel.setBackground(BG_PANEL);

        cbProveedor = new JComboBox<>();
        cbProducto = new JComboBox<>();
        txtCantidad = new JTextField("1");
        txtCosto = new JTextField("0.00");

        selectionPanel.add(new JLabel("Proveedor:"));
        selectionPanel.add(cbProveedor);
        selectionPanel.add(new JLabel("Producto:"));
        selectionPanel.add(cbProducto);
        selectionPanel.add(new JLabel("Cantidad:"));
        selectionPanel.add(txtCantidad);
        selectionPanel.add(new JLabel("Costo Unitario ($):"));
        selectionPanel.add(txtCosto);

        // Buttons & Voice
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(BG_PANEL);

        VoiceButton btnVoice = new VoiceButton();
        JButton btnAdd = createButton("Agregar al Pedido");

        btnPanel.add(btnVoice);
        btnPanel.add(btnAdd);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(BG_PANEL);
        topContainer.add(selectionPanel, BorderLayout.CENTER);
        topContainer.add(btnPanel, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH); // Placed at North inside the main North? No, let's fix layout.

        // Fix layout structure: Title NORTH, Form CENTER-TOP, Table CENTER.
        // Actually, let's put Form in NORTH (under title) and Table in CENTER.
        JPanel headerGroup = new JPanel(new BorderLayout());
        headerGroup.setBackground(BG_PANEL);
        headerGroup.add(lblTitle, BorderLayout.NORTH);
        headerGroup.add(topContainer, BorderLayout.CENTER);
        add(headerGroup, BorderLayout.NORTH);

        // Cart Table
        String[] columnNames = { "ID Prod", "Producto", "Costo Unit.", "Cantidad", "Subtotal" };
        cartModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartModel);
        // Hide ID
        cartTable.getColumnModel().getColumn(0).setMinWidth(0);
        cartTable.getColumnModel().getColumn(0).setMaxWidth(0);
        cartTable.getColumnModel().getColumn(0).setWidth(0);

        add(new JScrollPane(cartTable), BorderLayout.CENTER);

        // Action Panel (Bottom)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(BG_PANEL);

        txtTotal = new JTextField(10);
        txtTotal.setEditable(false);
        txtTotal.setText("0.00");

        JButton btnProcess = createButton("Procesar Compra");
        JButton btnClear = createButton("Limpiar");

        actionPanel.add(new JLabel("Total Compra: "));
        actionPanel.add(txtTotal);
        actionPanel.add(btnProcess);
        actionPanel.add(btnClear);
        add(actionPanel, BorderLayout.SOUTH);

        // Logic Listeners
        btnAdd.addActionListener(e -> addItemToCart());
        btnProcess.addActionListener(e -> procesarCompra());
        btnClear.addActionListener(e -> clearForm());

        // Update Cost when Product changes
        cbProducto.addActionListener(e -> {
            if (cbProducto.getSelectedIndex() >= 0 && cbProducto.getSelectedIndex() < listaProductos.size()) {
                Producto p = listaProductos.get(cbProducto.getSelectedIndex());
                txtCosto.setText(String.valueOf(p.getPrecioCompra()));
            }
        });

        // Focus Tracking
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", e -> {
            Component c = (Component) e.getNewValue();
            if (c instanceof JTextField) {
                btnVoice.setTargetComponent(c);
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
        cbProveedor.removeAllItems();
        cbProducto.removeAllItems();

        listaProveedores = proveedorDAO.listar();
        for (Proveedor p : listaProveedores) {
            cbProveedor.addItem(p.getNombre());
        }

        listaProductos = productoDAO.listar(); // Listar todos, o filtrar por proveedor si se desea lógica compleja
        for (Producto p : listaProductos) {
            cbProducto.addItem(p.getNombre());
        }
    }

    private void addItemToCart() {
        if (cbProducto.getSelectedIndex() == -1)
            return;

        try {
            int qty = Integer.parseInt(txtCantidad.getText());
            double cost = Double.parseDouble(txtCosto.getText());

            if (qty <= 0 || cost < 0) {
                JOptionPane.showMessageDialog(this, "Valores inválidos");
                return;
            }

            Producto p = listaProductos.get(cbProducto.getSelectedIndex());
            double subtotal = cost * qty;

            cartModel.addRow(new Object[] { p.getIdProducto(), p.getNombre(), cost, qty, subtotal });
            updateTotal();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese números válidos");
        }
    }

    private void updateTotal() {
        double total = 0;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            total += (double) cartModel.getValueAt(i, 4);
        }
        txtTotal.setText(String.format("%.2f", total));
    }

    private void clearForm() {
        cartModel.setRowCount(0);
        txtTotal.setText("0.00");
        txtCantidad.setText("1");
        if (cbProveedor.getItemCount() > 0)
            cbProveedor.setSelectedIndex(0);
        if (cbProducto.getItemCount() > 0)
            cbProducto.setSelectedIndex(0);
    }

    private void procesarCompra() {
        if (cartModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "La lista de compra está vacía");
            return;
        }

        if (cbProveedor.getSelectedIndex() == -1)
            return;
        Proveedor proveedor = listaProveedores.get(cbProveedor.getSelectedIndex());
        double total = Double.parseDouble(txtTotal.getText());
        String fecha = LocalDate.now().toString();

        CompraProveedor compra = new CompraProveedor();
        compra.setIdProveedor(proveedor.getIdProveedor());
        compra.setTotal(total);
        compra.setFecha(fecha);

        // 1. Insert Compra
        int idCompra = compraDAO.insertar(compra);
        if (idCompra != -1) {
            // 2. Insert Details & Update Stock
            boolean success = true;
            for (int i = 0; i < cartModel.getRowCount(); i++) {
                int idProd = (int) cartModel.getValueAt(i, 0);
                double costo = (double) cartModel.getValueAt(i, 2);
                int cant = (int) cartModel.getValueAt(i, 3);

                DetalleCompra detalle = new DetalleCompra();
                detalle.setIdCompra(idCompra);
                detalle.setIdProducto(idProd);
                detalle.setCantidad(cant);
                detalle.setPrecioUnitario(costo);

                if (!detalleDAO.insertar(detalle)) {
                    success = false;
                    // Consider transaction rollback logic here in a real app
                }

                if (!inventarioDAO.incrementarStock(idProd, cant)) {
                    success = false;
                }
            }

            if (success) {
                JOptionPane.showMessageDialog(this, "Compra registrada exitosamente!");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Hubo errores al registrar algunos detalles.");
            }

        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar la compra (Cabecera).");
        }
    }
}
