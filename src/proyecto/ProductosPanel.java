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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class ProductosPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNombre, txtDescripcion, txtPrecioVenta, txtPrecioCompra;
    private JTextField txtStock, txtMinimo;

    // Theme Colors
    private final Color BTN_DEFAULT = new Color(199, 179, 106);
    private final Color TXT_MAIN = new Color(60, 45, 20);

    public ProductosPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 240, 220));

        JLabel lblTitle = new JLabel("Gestión de Productos e Inventario");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 24));
        lblTitle.setForeground(TXT_MAIN);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        formPanel.setBackground(new Color(245, 240, 220));

        txtNombre = new JTextField();
        txtDescripcion = new JTextField();
        txtPrecioVenta = new JTextField();
        txtPrecioCompra = new JTextField();
        txtStock = new JTextField("0");
        txtMinimo = new JTextField("5");

        formPanel.add(new JLabel("Nombre:"));
        formPanel.add(txtNombre);
        formPanel.add(new JLabel("Descripción:"));
        formPanel.add(txtDescripcion);
        formPanel.add(new JLabel("Precio Venta:"));
        formPanel.add(txtPrecioVenta);
        formPanel.add(new JLabel("Precio Compra:"));
        formPanel.add(txtPrecioCompra);
        formPanel.add(new JLabel("Stock Actual:"));
        formPanel.add(txtStock);
        formPanel.add(new JLabel("Stock Mínimo:"));
        formPanel.add(txtMinimo);

        String[] columnNames = { "ID", "Nombre", "Descripción", "P. Venta", "P. Compra", "Stock", "Mínimo" };
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

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(new Color(245, 240, 220));

        JButton btnAdd = createButton("Agregar");
        JButton btnUpdate = createButton("Actualizar");
        JButton btnDelete = createButton("Eliminar");
        JButton btnClear = createButton("Limpiar");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        JPanel southContainer = new JPanel(new BorderLayout());
        southContainer.add(formPanel, BorderLayout.CENTER);
        southContainer.add(btnPanel, BorderLayout.SOUTH);
        add(southContainer, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addProducto());
        btnUpdate.addActionListener(e -> updateProducto());
        btnDelete.addActionListener(e -> deleteProducto());
        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                loadSelection();
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
        tableModel.setRowCount(0);
        String sql = "SELECT p.id_producto, p.nombre, p.descripcion, p.precio_venta, p.precio_compra, " +
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

            String sqlProd = "INSERT INTO Producto(nombre, descripcion, precio_venta, precio_compra) VALUES(?,?,?,?)";
            String sqlInv = "INSERT INTO Inventario(id_producto, cantidad_actual, minimo) VALUES(?,?,?)";

            try (Connection conn = DatabaseHelper.connect()) {
                conn.setAutoCommit(false);

                int idProducto = -1;
                try (PreparedStatement pstProd = conn.prepareStatement(sqlProd, Statement.RETURN_GENERATED_KEYS)) {
                    pstProd.setString(1, txtNombre.getText());
                    pstProd.setString(2, txtDescripcion.getText());
                    pstProd.setDouble(3, pVenta);
                    pstProd.setDouble(4, pCompra);
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

            String sqlProd = "UPDATE Producto SET nombre=?, descripcion=?, precio_venta=?, precio_compra=? WHERE id_producto=?";

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
                    pstProd.setDouble(3, pVenta);
                    pstProd.setDouble(4, pCompra);
                    pstProd.setInt(5, id);
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
        txtPrecioVenta.setText(tableModel.getValueAt(row, 3).toString());
        txtPrecioCompra.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
        txtStock.setText(tableModel.getValueAt(row, 5).toString());
        txtMinimo.setText(tableModel.getValueAt(row, 6).toString());
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
}
