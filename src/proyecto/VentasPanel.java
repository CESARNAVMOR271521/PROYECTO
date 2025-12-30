package proyecto;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import proyecto.vista.TicketWindow;
import proyecto.util.Theme;
import proyecto.util.ModernIcon.IconType;

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
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class VentasPanel extends JPanel implements VoiceAware {

    private JTable cartTable;
    private DefaultTableModel cartModel;
    private JComboBox<String> cbCliente, cbItem;
    private JComboBox<String> cbTipoItem; // Service or Product
    private JTextField txtCantidad, txtTotal;
    private JRadioButton rbEfectivo, rbTarjeta;
    private ButtonGroup bgPago;

    private ArrayList<Integer> clienteIds = new ArrayList<>();
    private ArrayList<Integer> itemIds = new ArrayList<>(); 
    private ArrayList<Double> itemPrices = new ArrayList<>();

    public VentasPanel() {
        setLayout(new BorderLayout(10, 10));
        Theme.applyTheme(this);

        JLabel lblTitle = new JLabel("Punto de Venta");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.COLOR_ACCENT_GOLD);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.COLOR_PRIMARY);
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Selection Panel
        JPanel selectionPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        TitledBorder selectionBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Theme.COLOR_ACCENT_GOLD), "Agregar Item");
        selectionBorder.setTitleColor(Theme.COLOR_ACCENT_GOLD);
        selectionBorder.setTitleFont(Theme.FONT_BOLD);
        
        selectionPanel.setBorder(selectionBorder);
        selectionPanel.setBackground(Theme.COLOR_SECONDARY);

        cbCliente = new JComboBox<>();
        cbTipoItem = new JComboBox<>(new String[] { "Servicio", "Producto" });
        cbItem = new JComboBox<>();
        txtCantidad = new JTextField("1");
        
        JButton btnAdd = Theme.createStyledButton("Agregar al Carrito", IconType.ADD);

        addLabel(selectionPanel, "Cliente:");
        selectionPanel.add(cbCliente);
        selectionPanel.add(new JLabel("")); // Spacer

        addLabel(selectionPanel, "Tipo:");
        selectionPanel.add(cbTipoItem);
        addLabel(selectionPanel, "Item:");
        
        selectionPanel.add(cbItem);
        addLabel(selectionPanel, "Cantidad:");
        selectionPanel.add(txtCantidad);

        // Cart Table
        String[] columnNames = { "Tipo", "ID Item", "Nombre", "Precio Unit.", "Cantidad", "Subtotal" };
        cartModel = new DefaultTableModel(columnNames, 0);
        cartTable = new JTable(cartModel);
        // Hide ID Item column (index 1)
        cartTable.getColumnModel().getColumn(1).setMinWidth(0);
        cartTable.getColumnModel().getColumn(1).setMaxWidth(0);
        cartTable.getColumnModel().getColumn(1).setWidth(0);
        
        Theme.styleTable(cartTable);

        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.getViewport().setBackground(Theme.COLOR_SECONDARY);

        // Action Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Theme.COLOR_PRIMARY);

        txtTotal = new JTextField(10);
        txtTotal.setEditable(false);
        txtTotal.setText("0.00");

        JButton btnProcess = Theme.createStyledButton("Procesar Venta", IconType.SAVE);
        JButton btnClear = Theme.createStyledButton("Cancelar", IconType.CANCEL);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(selectionPanel, BorderLayout.CENTER);
        JPanel addItemPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addItemPanel.setBackground(Theme.COLOR_SECONDARY);

        VoiceButton btnVoice = new VoiceButton();
        btnVoice.setBackground(Theme.COLOR_ACCENT_GOLD);
        btnVoice.setForeground(Theme.COLOR_PRIMARY);
        
        addItemPanel.add(btnVoice);
        addItemPanel.add(btnAdd);
        topContainer.add(addItemPanel, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        addLabel(actionPanel, "Total: ");
        actionPanel.add(txtTotal);

        // Payment Method
        rbEfectivo = new JRadioButton("Efectivo");
        rbTarjeta = new JRadioButton("Tarjeta");
        rbEfectivo.setBackground(Theme.COLOR_PRIMARY);
        rbEfectivo.setForeground(Theme.COLOR_TEXT);
        rbTarjeta.setBackground(Theme.COLOR_PRIMARY);
        rbTarjeta.setForeground(Theme.COLOR_TEXT);
        
        bgPago = new ButtonGroup();
        bgPago.add(rbEfectivo);
        bgPago.add(rbTarjeta);
        rbEfectivo.setSelected(true); // Default

        addLabel(actionPanel, " | Pago: ");
        actionPanel.add(rbEfectivo);
        actionPanel.add(rbTarjeta);

        actionPanel.add(btnProcess);
        actionPanel.add(btnClear);
        add(actionPanel, BorderLayout.SOUTH);

        // Listeners
        cbTipoItem.addActionListener(e -> loadItems());
        btnAdd.addActionListener(e -> addItemToCart());
        btnProcess.addActionListener(e -> processSale());
        btnClear.addActionListener(e -> clearSale());

        // Global Focus Tracking (Optional but helpful for robust voice target finding)
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", e -> {
            Component c = (Component) e.getNewValue();
            if (c instanceof JTextField) {
                btnVoice.setTargetComponent(c);
            }
        });

        loadClientes();
        loadItems(); // Initial load
        
        Theme.bindActionKeys(this, btnAdd, btnProcess, btnClear, null);
    }
    
    private void addLabel(JPanel panel, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Theme.COLOR_TEXT);
        lbl.setFont(Theme.FONT_BOLD);
        panel.add(lbl);
    }

    private void loadClientes() {
        cbCliente.removeAllItems();
        clienteIds.clear();
        cbCliente.addItem("Cliente Casual (Anonimo)");
        clienteIds.add(-1); // ID for anonymous

        try (Connection conn = DatabaseHelper.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id_cliente, nombre FROM Cliente")) {
            while (rs.next()) {
                clienteIds.add(rs.getInt("id_cliente"));
                cbCliente.addItem(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadItems() {
        cbItem.removeAllItems();
        itemIds.clear();
        itemPrices.clear();

        String type = (String) cbTipoItem.getSelectedItem();
        boolean isService = "Servicio".equals(type);

        String sql = isService ? "SELECT id_servicio as id, nombre, precio FROM Servicio"
                : "SELECT id_producto as id, nombre, precio_venta as precio FROM Producto";

        try (Connection conn = DatabaseHelper.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                itemIds.add(rs.getInt("id"));
                cbItem.addItem(rs.getString("nombre") + " ($" + rs.getDouble("precio") + ")");
                itemPrices.add(rs.getDouble("precio"));
            }
            System.out.println("VentasPanel: Loaded " + itemIds.size() + " items for type " + type);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addItemToCart() {
        if (cbItem.getSelectedIndex() == -1)
            return;

        try {
            int qty = Integer.parseInt(txtCantidad.getText());
            if (qty <= 0)
                throw new NumberFormatException();

            int index = cbItem.getSelectedIndex();
            int id = itemIds.get(index);
            double price = itemPrices.get(index);
            String name = (String) cbItem.getSelectedItem();
            name = name.substring(0, name.lastIndexOf(" ($")); // clean name
            String type = (String) cbTipoItem.getSelectedItem();
            double subtotal = price * qty;

            cartModel.addRow(new Object[] { type, id, name, price, qty, subtotal });
            updateTotal();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cantidad inválida");
        }
    }

    private void updateTotal() {
        double total = 0;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            total += (double) cartModel.getValueAt(i, 5);
        }
        txtTotal.setText(String.format("%.2f", total));
    }

    private void clearSale() {
        cartModel.setRowCount(0);
        txtTotal.setText("0.00");
        txtCantidad.setText("1");
        if (cbCliente.getItemCount() > 0)
            cbCliente.setSelectedIndex(0);
        if (rbEfectivo != null)
            rbEfectivo.setSelected(true);
    }

    private void processSale() {
        if (cartModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío");
            return;
        }

        int clienteIdx = cbCliente.getSelectedIndex();
        int idCliente = clienteIdx > 0 ? clienteIds.get(clienteIdx) : 0; 
        Double total = Double.parseDouble(txtTotal.getText());
        String fecha = LocalDate.now().toString();

        double montoRecibido = total;
        double cambio = 0.0;

        // New Logic for Cash Payment Amount
        if (rbEfectivo.isSelected()) {
            String input = JOptionPane.showInputDialog(this, "Total: $" + total + "\nIngrese monto recibido:", "Pago en Efectivo", JOptionPane.QUESTION_MESSAGE);
            
            if (input == null || input.trim().isEmpty()) {
                return; // Cancelled
            }
            
            try {
                montoRecibido = Double.parseDouble(input);
                
                if (montoRecibido < total) {
                    JOptionPane.showMessageDialog(this, "Monto insuficiente. Faltan: $" + String.format("%.2f", total - montoRecibido), "Error de Pago", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                cambio = montoRecibido - total;
                JOptionPane.showMessageDialog(this, "Venta Exitosa.\nSu cambio: $" + String.format("%.2f", cambio), "Pago Completado", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Monto inválido. Ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try (Connection conn = DatabaseHelper.connect()) {
            conn.setAutoCommit(false); // Transaction

            try {
                // 1. Create Sale
                String sqlVenta = "INSERT INTO Venta(fecha, id_cliente, total, tipo) VALUES(?, ?, ?, ?)";
                int idVenta = -1;

                PreparedStatement pstVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
                pstVenta.setString(1, fecha);
                if (idCliente > 0)
                    pstVenta.setInt(2, idCliente);
                else
                    pstVenta.setNull(2, java.sql.Types.INTEGER);
                pstVenta.setDouble(3, total);
                pstVenta.setString(4, "mixto"); // Can be refined
                pstVenta.executeUpdate();

                ResultSet rs = pstVenta.getGeneratedKeys();
                if (rs.next())
                    idVenta = rs.getInt(1);
                rs.close();
                pstVenta.close();

                // 2. Create Details
                String sqlDetalle = "INSERT INTO DetalleVenta(id_venta, id_producto, id_servicio, cantidad, precio_unitario) VALUES(?,?,?,?,?)";
                PreparedStatement pstDetalle = conn.prepareStatement(sqlDetalle);

                for (int i = 0; i < cartModel.getRowCount(); i++) {
                    String type = (String) cartModel.getValueAt(i, 0);
                    int idItem = (int) cartModel.getValueAt(i, 1);
                    int qty = (int) cartModel.getValueAt(i, 4);
                    double price = (double) cartModel.getValueAt(i, 3);

                    pstDetalle.setInt(1, idVenta);
                    if ("Producto".equals(type)) {
                        pstDetalle.setInt(2, idItem);
                        pstDetalle.setNull(3, java.sql.Types.INTEGER);

                        // Update Inventory (Now in Producto)
                        PreparedStatement pstInv = conn.prepareStatement(
                                "UPDATE Producto SET cantidad_actual = cantidad_actual - ? WHERE id_producto = ?");
                        pstInv.setInt(1, qty);
                        pstInv.setInt(2, idItem);
                        pstInv.executeUpdate();
                        pstInv.close();

                    } else {
                        pstDetalle.setNull(2, java.sql.Types.INTEGER);
                        pstDetalle.setInt(3, idItem);
                    }
                    pstDetalle.setInt(4, qty);
                    pstDetalle.setDouble(5, price);
                    pstDetalle.addBatch();
                }
                pstDetalle.executeBatch();
                pstDetalle.close();

                // 3. Create Factura (simplified)
                String sqlFactura = "INSERT INTO Factura(id_venta, fecha_emision, total) VALUES(?,?,?)";
                PreparedStatement pstFactura = conn.prepareStatement(sqlFactura);
                pstFactura.setInt(1, idVenta);
                pstFactura.setString(2, fecha);
                pstFactura.setDouble(3, total);
                pstFactura.executeUpdate();
                pstFactura.close();

                // 4. Register Payment
                String metodo = rbEfectivo.isSelected() ? "Efectivo" : "Tarjeta";
                String sqlPago = "INSERT INTO Pago(id_venta, forma_pago, monto, estado) VALUES(?, ?, ?, ?)";
                PreparedStatement pstPago = conn.prepareStatement(sqlPago);
                pstPago.setInt(1, idVenta);
                pstPago.setString(2, metodo);
                pstPago.setDouble(3, total);
                pstPago.setString(4, "Completado");
                pstPago.executeUpdate();
                pstPago.close();

                conn.commit();

                // Show Ticket
                final double finalMontoRecibido = montoRecibido;
                final double finalCambio = cambio;
                SwingUtilities.invokeLater(() -> {
                    new TicketWindow(
                            SwingUtilities.getWindowAncestor(this),
                            (String) cbCliente.getSelectedItem(),
                            cartModel,
                            total,
                            metodo, 
                            finalMontoRecibido, 
                            finalCambio).setVisible(true);
                });

                // JOptionPane.showMessageDialog(this, "Venta realizada con éxito");
                clearSale();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en la venta: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @Override
    public void handleVoiceCommand(String command, String args) {
        System.out.println("VentasPanel Handling: " + command + " [" + args + "]");
        
        switch (command) {
            case "AGREGA":
                if (args == null || args.isEmpty()) return;
                // Fuzzy search in Dropdown
                String search = args.toLowerCase();
                int bestMatch = -1;
                
                for (int i = 0; i < cbItem.getItemCount(); i++) {
                    String itemText = cbItem.getItemAt(i).toLowerCase();
                    if (itemText.contains(search)) {
                        bestMatch = i;
                        break; // Pick first containment match
                    }
                }
                
                if (bestMatch != -1) {
                    cbItem.setSelectedIndex(bestMatch);
                    // Automatically add if high confidence or user said "Agrega"
                    addItemToCart();
                } else {
                    System.out.println("Item no encontrado: " + args);
                }
                break;
            
            case "SET_PAYMENT":
                if (args.equalsIgnoreCase("TARJETA")) {
                    rbTarjeta.setSelected(true);
                } else {
                    rbEfectivo.setSelected(true);
                }
                break;

            case "PROCESS_SALE":
            case "COBRAR": // Alias
                processSale();
                break;

            case "DELETE": 
                // "Borra el ultimo" or "Quita corte"
                // For now, if args.contains("TODO"), clear sale.
                if (args.contains("TODO") || args.contains("VENTA")) {
                    clearSale();
                } else {
                    // Try to remove selected row, or last row if none selected
                    int row = cartTable.getSelectedRow();
                    if (row == -1 && cartTable.getRowCount() > 0) {
                        row = cartTable.getRowCount() - 1;
                    }
                    if (row != -1) {
                         cartModel.removeRow(row);
                         updateTotal();
                    }
                }
                break;
                
            case "CLEAR":
                clearSale();
                break;
                
            case "AUMENTA":
                try {
                    int delta = 1;
                    if (args != null && !args.isEmpty()) {
                        delta = Integer.parseInt(args.replaceAll("[^0-9]", ""));
                    }
                    // If text field has focus, maybe just set it? 
                    // Default behavior: Set Quantity Field
                    int current = 1;
                    try { current = Integer.parseInt(txtCantidad.getText()); } catch(Exception e){}
                    
                    // Interpret input as "Start with X" or "Add X"? 
                    // Let's assume user says "Aumenta 3" implies "Set quantity to 3" or "Add 3"?
                    // Actually prompt says "Aumenta", implies increase. 
                    // But if user says "Aumenta 5", normally they mean "I want 5 of these".
                    // Let's set it to the number provided if > 0, else increment.
                    
                    if (delta > 0 && args.matches(".*\\d.*")) {
                         txtCantidad.setText(String.valueOf(delta));
                    } else {
                         txtCantidad.setText(String.valueOf(current + 1));
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
                
            case "REGISTRA":
                processSale();
                break;
        }
    }
}


