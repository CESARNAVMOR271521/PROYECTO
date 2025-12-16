package proyecto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
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

public class VentasPanel extends JPanel {

    private JTable cartTable;
    private DefaultTableModel cartModel;
    private JComboBox<String> cbCliente, cbItem;
    private JComboBox<String> cbTipoItem; // Service or Product
    private JTextField txtCantidad, txtTotal;
    private JRadioButton rbEfectivo, rbTarjeta;
    private ButtonGroup bgPago;

    private ArrayList<Integer> clienteIds = new ArrayList<>();
    private ArrayList<Integer> itemIds = new ArrayList<>(); // IDs of currently loaded items (products or services)
    private ArrayList<Double> itemPrices = new ArrayList<>();

    private final Color BTN_DEFAULT = new Color(199, 179, 106);
    private final Color TXT_MAIN = new Color(60, 45, 20);

    public VentasPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 240, 220));

        JLabel lblTitle = new JLabel("Punto de Venta");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 24));
        lblTitle.setForeground(TXT_MAIN);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        // Selection Panel
        JPanel selectionPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Agregar Item"));
        selectionPanel.setBackground(new Color(245, 240, 220));

        cbCliente = new JComboBox<>();
        cbTipoItem = new JComboBox<>(new String[] { "Servicio", "Producto" });
        cbItem = new JComboBox<>();
        txtCantidad = new JTextField("1");

        JButton btnAdd = createButton("Agregar al Carrito");

        selectionPanel.add(new JLabel("Cliente:"));
        selectionPanel.add(cbCliente);
        selectionPanel.add(new JLabel("")); // Spacer

        selectionPanel.add(new JLabel("Tipo:"));
        selectionPanel.add(cbTipoItem);
        selectionPanel.add(new JLabel("Item:"));

        selectionPanel.add(cbItem);
        selectionPanel.add(new JLabel("Cantidad:"));
        selectionPanel.add(txtCantidad);

        // Cart Table
        String[] columnNames = { "Tipo", "ID Item", "Nombre", "Precio Unit.", "Cantidad", "Subtotal" };
        cartModel = new DefaultTableModel(columnNames, 0);
        cartTable = new JTable(cartModel);
        // Hide ID Item column (index 1)
        cartTable.getColumnModel().getColumn(1).setMinWidth(0);
        cartTable.getColumnModel().getColumn(1).setMaxWidth(0);
        cartTable.getColumnModel().getColumn(1).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(cartTable);

        // Action Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(new Color(245, 240, 220));

        txtTotal = new JTextField(10);
        txtTotal.setEditable(false);
        txtTotal.setText("0.00");

        JButton btnProcess = createButton("Procesar Venta");
        JButton btnClear = createButton("Cancelar");
        JButton btnAddCart = createButton("Agregar Item"); // Moving button here for better layout flow or keeping in
                                                           // grid

        // Re-adding Add button to selection panel as intended above
        // selectionPanel.add(btnAdd); // This was missing in the grid layout count
        // logic if I wanted it there

        // Let's reorganize slightly
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(selectionPanel, BorderLayout.CENTER);
        JPanel addItemPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addItemPanel.setBackground(new Color(245, 240, 220));

        // Add Voice Button
        VoiceButton btnVoice = new VoiceButton();
        addItemPanel.add(btnVoice);

        addItemPanel.add(btnAdd);
        topContainer.add(addItemPanel, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        actionPanel.add(new JLabel("Total: "));
        actionPanel.add(txtTotal);

        // Payment Method
        rbEfectivo = new JRadioButton("Efectivo");
        rbTarjeta = new JRadioButton("Tarjeta");
        bgPago = new ButtonGroup();
        bgPago.add(rbEfectivo);
        bgPago.add(rbTarjeta);
        rbEfectivo.setSelected(true); // Default

        actionPanel.add(new JLabel(" | Pago: "));
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
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(BTN_DEFAULT);
        btn.setForeground(TXT_MAIN);
        btn.setFocusPainted(false);
        return btn;
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
        int idCliente = clienteIdx > 0 ? clienteIds.get(clienteIdx) : 0; // 0 for NULL/Anon if logic allows, or handle
                                                                         // strictly
        Double total = Double.parseDouble(txtTotal.getText());
        String fecha = LocalDate.now().toString();

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

                        // Update Inventory
                        PreparedStatement pstInv = conn.prepareStatement(
                                "UPDATE Inventario SET cantidad_actual = cantidad_actual - ? WHERE id_producto = ?");
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
                SwingUtilities.invokeLater(() -> {
                    new TicketWindow(
                            SwingUtilities.getWindowAncestor(this),
                            (String) cbCliente.getSelectedItem(),
                            cartModel,
                            total,
                            metodo).setVisible(true);
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
}
