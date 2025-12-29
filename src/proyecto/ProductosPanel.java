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
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import proyecto.util.Theme;
import proyecto.util.ModernIcon.IconType;

public class ProductosPanel extends JPanel implements VoiceAware {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNombre, txtDescripcion, txtPrecioVenta, txtPrecioCompra;
    private JTextField txtStock, txtMinimo;
    private JComboBox<String> cbCategoria;

    public ProductosPanel() {
        setLayout(new BorderLayout(10, 10));
        Theme.applyTheme(this);

        JLabel lblTitle = new JLabel("Gestión de Productos e Inventario");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.COLOR_ACCENT_GOLD);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Header Panel with Title and Help Button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.COLOR_PRIMARY);
        headerPanel.add(lblTitle, BorderLayout.CENTER);

        JButton btnHelp = new JButton("?");
        btnHelp.setFont(Theme.FONT_BOLD);
        btnHelp.setBackground(Theme.COLOR_PRIMARY);
        btnHelp.setForeground(Theme.COLOR_ACCENT_GOLD);
        btnHelp.setBorder(BorderFactory.createLineBorder(Theme.COLOR_ACCENT_GOLD));
        btnHelp.setFocusPainted(false);
        btnHelp.setPreferredSize(new java.awt.Dimension(30, 30));
        btnHelp.setToolTipText("Ayuda para nuevos usuarios");

        JPanel helpContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        helpContainer.setBackground(Theme.COLOR_PRIMARY);
        helpContainer.add(btnHelp);
        headerPanel.add(helpContainer, BorderLayout.EAST);

        btnHelp.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "<html><body><h3>Guía Rápida:</h3>" +
                            "<ul>" +
                            "<li><b>Buscar:</b> Use la barra superior para filtrar productos por nombre.</li>" +
                            "<li><b>Agregar:</b> Llene los campos y presione 'Agregar'.</li>" +
                            "<li><b>Editar:</b> Seleccione un producto de la tabla, modifique y presione 'Actualizar'.</li>"
                            +
                            "<li><b>Voz:</b> Presione el micrófono para dictar en el campo seleccionado.</li>" +
                            "</ul></body></html>",
                    "Ayuda", JOptionPane.INFORMATION_MESSAGE);
        });

        add(headerPanel, BorderLayout.NORTH);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Theme.COLOR_PRIMARY);
        JLabel lblSearch = new JLabel("Buscar Producto:");
        lblSearch.setForeground(Theme.COLOR_TEXT);
        searchPanel.add(lblSearch);
        
        JTextField txtSearch = new JTextField(20);
        searchPanel.add(txtSearch);

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String text = txtSearch.getText();
                javax.swing.table.TableRowSorter<DefaultTableModel> sorter = (javax.swing.table.TableRowSorter<DefaultTableModel>) table
                        .getRowSorter();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + text, 1));
                }
            }
        });

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Main form container
        JPanel formContainer = new JPanel(new GridLayout(1, 3, 10, 0));
        formContainer.setBackground(Theme.COLOR_SECONDARY);
        formContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Group 1: Product Data
        JPanel groupProducto = createGroupPanel("Datos del Producto");
        txtNombre = new JTextField();
        txtNombre.setToolTipText("Ingrese el nombre comercial del producto");
        txtDescripcion = new JTextField();
        txtDescripcion.setToolTipText("Describa brevemente el producto");
        addLabel(groupProducto, "Nombre:");
        groupProducto.add(txtNombre);
        addLabel(groupProducto, "Descripción:");
        groupProducto.add(txtDescripcion);
        
        cbCategoria = new JComboBox<>(new String[]{"Cabello", "Barba", "Afeitado", "Herramientas", "Otros"});
        addLabel(groupProducto, "Categoría:");
        groupProducto.add(cbCategoria);

        // Group 2: Pricing
        JPanel groupPrecios = createGroupPanel("Precios");
        txtPrecioVenta = new JTextField();
        txtPrecioVenta.setToolTipText("Precio al público");
        txtPrecioCompra = new JTextField();
        txtPrecioCompra.setToolTipText("Costo de adquisición");
        addLabel(groupPrecios, "Precio Venta:");
        groupPrecios.add(txtPrecioVenta);
        addLabel(groupPrecios, "Precio Compra:");
        groupPrecios.add(txtPrecioCompra);

        // Group 3: Inventory
        JPanel groupInventario = createGroupPanel("Inventario");
        txtStock = new JTextField("0");
        txtStock.setToolTipText("Cantidad física actual");
        txtMinimo = new JTextField("5");
        txtMinimo.setToolTipText("Alerta de stock bajo");
        addLabel(groupInventario, "Stock Actual:");
        groupInventario.add(txtStock);
        addLabel(groupInventario, "Stock Mínimo:");
        groupInventario.add(txtMinimo);

        formContainer.add(groupProducto);
        formContainer.add(groupPrecios);
        formContainer.add(groupInventario);

        String[] columnNames = { "ID", "Nombre", "Descripción", "Cat.", "P. Venta", "P. Compra", "Stock", "Mínimo" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        Theme.styleTable(table);

        javax.swing.table.TableRowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Theme.COLOR_SECONDARY);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(Theme.COLOR_PRIMARY);

        JButton btnAdd = Theme.createStyledButton("Agregar", IconType.ADD);
        JButton btnUpdate = Theme.createStyledButton("Actualizar", IconType.EDIT);
        JButton btnDelete = Theme.createStyledButton("Eliminar", IconType.DELETE);
        JButton btnClear = Theme.createStyledButton("Limpiar", IconType.CLEAR);

        VoiceButton btnVoice = new VoiceButton();
        btnVoice.setBackground(Theme.COLOR_ACCENT_GOLD);
        btnVoice.setForeground(Theme.COLOR_PRIMARY);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        btnPanel.add(btnVoice);

        JPanel mainSouthPanel = new JPanel(new BorderLayout());
        mainSouthPanel.add(formContainer, BorderLayout.CENTER);
        mainSouthPanel.add(btnPanel, BorderLayout.SOUTH);
        add(mainSouthPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addProducto());
        btnUpdate.addActionListener(e -> updateProducto());
        btnDelete.addActionListener(e -> deleteProducto());
        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                loadSelection();
            }
        });

        // Global Focus Tracking
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", e -> {
            java.awt.Component c = (java.awt.Component) e.getNewValue();
            if (c instanceof JTextField) {
                btnVoice.setTargetComponent(c);
            }
        });
        
        loadData();
        
        Theme.bindActionKeys(this, btnAdd, btnUpdate, btnDelete, btnClear);
    }

    private JPanel createGroupPanel(String title) {
        JPanel panel = new JPanel(new GridLayout(5, 1, 5, 5));
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Theme.COLOR_ACCENT_GOLD), title);
        border.setTitleColor(Theme.COLOR_ACCENT_GOLD);
        border.setTitleFont(Theme.FONT_BOLD);
        panel.setBorder(border);
        panel.setBackground(Theme.COLOR_SECONDARY);
        return panel;
    }

    private void addLabel(JPanel panel, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Theme.COLOR_TEXT);
        lbl.setFont(Theme.FONT_BOLD);
        panel.add(lbl);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        String sql = "SELECT p.id_producto, p.nombre, p.descripcion, p.categoria, p.precio_venta, p.precio_compra, " +
                "COALESCE(i.cantidad_actual, 0) as stock, COALESCE(i.minimo, 0) as minimo " +
                "FROM Producto p " +
                "LEFT JOIN Inventario i ON p.id_producto = i.id_producto";

        try (Connection conn = DatabaseHelper.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getString("categoria"),
                        rs.getDouble("precio_venta"),
                        rs.getDouble("precio_compra"),
                        rs.getInt("stock"),
                        rs.getInt("minimo")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando datos: " + e.getMessage());
        }
    }

    private void addProducto() {
        try {
            double pVenta = Double.parseDouble(txtPrecioVenta.getText());
            double pCompra = txtPrecioCompra.getText().isEmpty() ? 0 : Double.parseDouble(txtPrecioCompra.getText());
            int stock = Integer.parseInt(txtStock.getText());
            int minimo = Integer.parseInt(txtMinimo.getText());

            String sqlProd = "INSERT INTO Producto(nombre, descripcion, categoria, precio_venta, precio_compra) VALUES(?,?,?,?,?)";
            String sqlInv = "INSERT INTO Inventario(id_producto, cantidad_actual, minimo) VALUES(?,?,?)";

            try (Connection conn = DatabaseHelper.connect()) {
                conn.setAutoCommit(false);

                int idProducto = -1;
                try (PreparedStatement pstProd = conn.prepareStatement(sqlProd, Statement.RETURN_GENERATED_KEYS)) {
                    pstProd.setString(1, txtNombre.getText());
                    pstProd.setString(2, txtDescripcion.getText());
                    pstProd.setString(3, (String) cbCategoria.getSelectedItem());
                    pstProd.setDouble(4, pVenta);
                    pstProd.setDouble(5, pCompra);
                    pstProd.executeUpdate();

                    ResultSet rs = pstProd.getGeneratedKeys();
                    if (rs.next())
                        idProducto = rs.getInt(1);
                }

                if (idProducto != -1) {
                    try (PreparedStatement pstInv = conn.prepareStatement(sqlInv)) {
                        pstInv.setInt(1, idProducto);
                        pstInv.setInt(2, stock);
                        pstInv.setInt(3, minimo);
                        pstInv.executeUpdate();
                    }
                }

                conn.commit();
                loadData();
                clearForm();
                JOptionPane.showMessageDialog(this, "Producto agregado correctamente.");

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al guardar (transacción): " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valores numéricos inválidos");
        }
    }

    private void updateProducto() {
        int row = table.getSelectedRow();
        if (row == -1)
            return;
        int id = (int) tableModel.getValueAt(row, 0);

        try {
            double pVenta = Double.parseDouble(txtPrecioVenta.getText());
            double pCompra = txtPrecioCompra.getText().isEmpty() ? 0 : Double.parseDouble(txtPrecioCompra.getText());
            int stock = Integer.parseInt(txtStock.getText());
            int minimo = Integer.parseInt(txtMinimo.getText());

            String sqlProd = "UPDATE Producto SET nombre=?, descripcion=?, categoria=?, precio_venta=?, precio_compra=? WHERE id_producto=?";

            // Check if inventory row exists
            boolean invExists = false;
            try (Connection conn = DatabaseHelper.connect();
                    PreparedStatement check = conn.prepareStatement("SELECT 1 FROM Inventario WHERE id_producto=?")) {
                check.setInt(1, id);
                invExists = check.executeQuery().next();
            }

            String sqlInv = invExists
                    ? "UPDATE Inventario SET cantidad_actual=?, minimo=? WHERE id_producto=?"
                    : "INSERT INTO Inventario(cantidad_actual, minimo, id_producto) VALUES(?,?,?)";

            try (Connection conn = DatabaseHelper.connect()) {
                conn.setAutoCommit(false);

                try (PreparedStatement pstProd = conn.prepareStatement(sqlProd)) {
                    pstProd.setString(1, txtNombre.getText());
                    pstProd.setString(2, txtDescripcion.getText());
                    pstProd.setString(3, (String) cbCategoria.getSelectedItem());
                    pstProd.setDouble(4, pVenta);
                    pstProd.setDouble(5, pCompra);
                    pstProd.setInt(6, id);
                    pstProd.executeUpdate();
                }

                try (PreparedStatement pstInv = conn.prepareStatement(sqlInv)) {
                    pstInv.setInt(1, stock);
                    pstInv.setInt(2, minimo);
                    pstInv.setInt(3, id);
                    pstInv.executeUpdate();
                }

                conn.commit();
                loadData();
                clearForm();
                JOptionPane.showMessageDialog(this, "Producto actualizado correctamente.");

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al actualizar (transacción): " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valores numéricos inválidos");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de BD: " + e.getMessage());
        }
    }

    private void deleteProducto() {
        int row = table.getSelectedRow();
        if (row == -1)
            return;
        int id = (int) tableModel.getValueAt(row, 0);

        if (JOptionPane.showConfirmDialog(this, "¿Seguro de eliminar? Esto eliminará también el inventario.",
                "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseHelper.connect()) {
                conn.setAutoCommit(false);

                try (PreparedStatement pstInv = conn.prepareStatement("DELETE FROM Inventario WHERE id_producto=?")) {
                    pstInv.setInt(1, id);
                    pstInv.executeUpdate();
                }

                try (PreparedStatement pstProd = conn.prepareStatement("DELETE FROM Producto WHERE id_producto=?")) {
                    pstProd.setInt(1, id);
                    pstProd.executeUpdate();
                }

                conn.commit();
                loadData();
                clearForm();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage());
            }
        }
    }

    private void loadSelection() {
        int row = table.getSelectedRow();
        txtNombre.setText(tableModel.getValueAt(row, 1).toString());
        txtDescripcion.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");
        if (tableModel.getValueAt(row, 3) != null) cbCategoria.setSelectedItem(tableModel.getValueAt(row, 3).toString());
        txtPrecioVenta.setText(tableModel.getValueAt(row, 4).toString());
        txtPrecioCompra.setText(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "");
        txtStock.setText(tableModel.getValueAt(row, 6).toString());
        txtMinimo.setText(tableModel.getValueAt(row, 7).toString());
    }

    private void clearForm() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecioVenta.setText("");
        txtPrecioCompra.setText("");
        txtStock.setText("0");
        txtMinimo.setText("5");
        table.clearSelection();
    }
    @Override
    public void handleVoiceCommand(String command, String args) {
        switch (command) {
            case "CLEAR":
                clearForm();
                break;
            case "CREATE":
                addProducto();
                break;
            case "UPDATE":
                updateProducto();
                break;
            case "DELETE":
                deleteProducto();
                break;
            case "SEARCH":
                // ...
                break;
            case "SELECT":
                if (args == null || args.isEmpty()) return;
                String query = args.toLowerCase();
                for (int i = 0; i < table.getRowCount(); i++) {
                    // Column 1 is Nombre
                    String name = table.getValueAt(i, 1).toString().toLowerCase();
                    if (name.contains(query)) {
                        table.setRowSelectionInterval(i, i);
                        table.scrollRectToVisible(table.getCellRect(i, 0, true));
                        loadSelection();
                        break;
                    }
                }
                break;
            case "SET_FIELD":
                String[] parts = args.split(" ", 2);
                if (parts.length < 2) return;
                String field = parts[0].toUpperCase();
                String val = parts[1];
                
                switch (field) {
                    case "NOMBRE": txtNombre.setText(val); break;
                    case "DESCRIPCION": txtDescripcion.setText(val); break;
                    case "PRECIO": 
                        // Try to handle "PRECIO VENTA" or just "PRECIO"
                        if (val.toUpperCase().contains("COMPRA")) {
                             txtPrecioCompra.setText(val.replaceAll("[^0-9.]", ""));
                        } else {
                             txtPrecioVenta.setText(val.replaceAll("[^0-9.]", ""));
                        }
                        break;
                    case "STOCK": txtStock.setText(val.replaceAll("[^0-9]", "")); break;
                }
                break;
            default:
                System.out.println("Comando no soportado en Productos: " + command);
        }
    }
}

