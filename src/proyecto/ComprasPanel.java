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
import proyecto.util.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ComprasPanel extends JPanel {

    private CompraProveedorDAO compraDAO;
    private DetalleCompraDAO detalleDAO;
    private InventarioDAO inventarioDAO;
    private ProveedorDAO proveedorDAO;
    private ProductoDAO productoDAO;

    private JComboBox<String> cbProveedor;
    private JComboBox<String> cbProducto;
    private JTextField txtCantidad;
    private JTextField txtCosto;
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
        Theme.applyTheme(this);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.COLOR_PRIMARY);

        JLabel lblTitle = new JLabel("Registro de Compras a Proveedores");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.COLOR_ACCENT_GOLD);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(lblTitle, BorderLayout.NORTH);

        // Selection Panel
        JPanel selectionPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        selectionPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.COLOR_ACCENT_GOLD), "Agregar Producto",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                Theme.FONT_REGULAR, Theme.COLOR_ACCENT_GOLD));
        selectionPanel.setBackground(Theme.COLOR_SECONDARY);

        cbProveedor = new JComboBox<>();
        cbProducto = new JComboBox<>();
        txtCantidad = new JTextField("1");
        txtCosto = new JTextField("0.00");

        addLabel(selectionPanel, "Proveedor:");
        selectionPanel.add(cbProveedor);
        addLabel(selectionPanel, "Producto:");
        selectionPanel.add(cbProducto);
        addLabel(selectionPanel, "Cantidad:");
        selectionPanel.add(txtCantidad);
        addLabel(selectionPanel, "Costo Unitario ($):");
        selectionPanel.add(txtCosto);

        // Buttons & Voice
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Theme.COLOR_SECONDARY);

        VoiceButton btnVoice = new VoiceButton();
        btnVoice.setBackground(Theme.COLOR_ACCENT_GOLD);
        btnVoice.setForeground(Theme.COLOR_PRIMARY);
        
        JButton btnAdd = Theme.createStyledButton("Agregar al Pedido");

        btnPanel.add(btnVoice);
        btnPanel.add(btnAdd);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(Theme.COLOR_SECONDARY);
        topContainer.add(selectionPanel, BorderLayout.CENTER);
        topContainer.add(btnPanel, BorderLayout.SOUTH);
        
        headerPanel.add(topContainer, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

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

        Theme.styleTable(cartTable);
        
        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.getViewport().setBackground(Theme.COLOR_SECONDARY);
        add(scrollPane, BorderLayout.CENTER);

        // Action Panel (Bottom)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Theme.COLOR_PRIMARY);

        txtTotal = new JTextField(10);
        txtTotal.setEditable(false);
        txtTotal.setText("0.00");

        JButton btnProcess = Theme.createStyledButton("Procesar Compra");
        JButton btnClear = Theme.createStyledButton("Limpiar");

        JLabel lblTotal = new JLabel("Total Compra: ");
        lblTotal.setForeground(Theme.COLOR_TEXT);
        lblTotal.setFont(Theme.FONT_BOLD);
        actionPanel.add(lblTotal);
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

    private void addLabel(JPanel panel, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Theme.COLOR_TEXT);
        lbl.setFont(Theme.FONT_BOLD);
        panel.add(lbl);
    }

    private void loadData() {
        cbProveedor.removeAllItems();
        cbProducto.removeAllItems();

        listaProveedores = proveedorDAO.listar();
        for (Proveedor p : listaProveedores) {
            cbProveedor.addItem(p.getNombre());
        }

        listaProductos = productoDAO.listar(); 
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
