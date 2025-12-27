package proyecto;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
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

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import proyecto.dao.DetalleVentaDAO;
import proyecto.dao.FacturaDAO;
import proyecto.modelo.DetalleVenta;
import proyecto.util.Theme;

public class FacturasPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtBuscar;
    private FacturaDAO facturaDAO;
    private DetalleVentaDAO detalleDAO;

    public FacturasPanel() {
        this.facturaDAO = new FacturaDAO();
        this.detalleDAO = new DetalleVentaDAO();

        setLayout(new BorderLayout(10, 10));
        Theme.applyTheme(this);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.COLOR_PRIMARY);

        JLabel lblTitle = new JLabel("Gestión de Facturas");
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

        // Table
        String[] columns = { "ID Factura", "ID Venta", "Fecha", "Cliente", "Total" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);

        // Hide ID Factura (0) and ID Venta (1)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
        table.getColumnModel().getColumn(1).setWidth(0);

        Theme.styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Theme.COLOR_SECONDARY);
        add(scrollPane, BorderLayout.CENTER);

        // Sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
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

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Theme.COLOR_PRIMARY);

        JButton btnPrint = Theme.createStyledButton("Imprimir PDF");
        btnPrint.addActionListener(e -> printSelectedFactura());

        JButton btnRefresh = Theme.createStyledButton("Actualizar");
        btnRefresh.addActionListener(e -> loadFacturas());

        VoiceButton btnVoice = new VoiceButton();
        btnVoice.setBackground(Theme.COLOR_ACCENT_GOLD);
        btnVoice.setForeground(Theme.COLOR_PRIMARY);

        btnPanel.add(btnRefresh);
        btnPanel.add(btnPrint);
        btnPanel.add(btnVoice);
        add(btnPanel, BorderLayout.SOUTH);

        // Global Focus Tracking
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", e -> {
            java.awt.Component c = (java.awt.Component) e.getNewValue();
            if (c instanceof JTextField) {
                btnVoice.setTargetComponent(c);
            }
        });

        loadFacturas();
    }

    private void loadFacturas() {
        model.setRowCount(0);
        List<Map<String, Object>> facturas = facturaDAO.listarConCliente();
        for (Map<String, Object> f : facturas) {
            model.addRow(new Object[] {
                    f.get("id_factura"),
                    f.get("id_venta"),
                    f.get("fecha"),
                    f.get("cliente"),
                    f.get("total")
            });
        }
    }

    private void printSelectedFactura() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura para imprimir.");
            return;
        }

        int idFactura = (int) model.getValueAt(selectedRow, 0);
        int idVenta = (int) model.getValueAt(selectedRow, 1);
        String fecha = (String) model.getValueAt(selectedRow, 2);
        String cliente = (String) model.getValueAt(selectedRow, 3);
        double total = (double) model.getValueAt(selectedRow, 4);

        generatePDF(idFactura, idVenta, fecha, cliente, total);
    }

    private void generatePDF(int idFactura, int idVenta, String fecha, String cliente, double total) {
        Document document = new Document();
        try {
            String filename = "Factura_" + idFactura + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();

            // Logo
            try {
                com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance("src/img/logo.jpg");
                logo.scaleToFit(100, 100);
                logo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                document.add(logo);
            } catch (Exception e) {
                // Ignore if logo fails
            }

            // Header
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
            Paragraph title = new Paragraph("BARBERÍA CHUPIRULES - FACTURA", titleFont);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Spacer

            // Info
            document.add(new Paragraph("Factura #: " + idFactura));
            document.add(new Paragraph("Fecha: " + fecha));
            document.add(new Paragraph("Cliente: " + cliente));
            document.add(new Paragraph(" "));

            // Details Table
            PdfPTable pdfTable = new PdfPTable(4); // Cant, Desc, Precio, Subtotal
            pdfTable.addCell("Cant.");
            pdfTable.addCell("Descripción");
            pdfTable.addCell("Precio Unit.");
            pdfTable.addCell("Subtotal");

            List<DetalleVenta> detalles = detalleDAO.listarPorVenta(idVenta);

            for (DetalleVenta d : detalles) {
                String itemName = getItemName(d.getTipoItem(), d.getIdItem());

                pdfTable.addCell(String.valueOf(d.getCantidad()));
                pdfTable.addCell(itemName);
                pdfTable.addCell(String.format("$%.2f", d.getPrecioUnitario()));
                pdfTable.addCell(String.format("$%.2f", d.getSubtotal()));
            }

            document.add(pdfTable);
            document.add(new Paragraph(" "));

            // Total
            Paragraph pTotal = new Paragraph("TOTAL: $" + String.format("%.2f", total));
            pTotal.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
            document.add(pTotal);

            document.close();
            JOptionPane.showMessageDialog(this, "Factura generada: " + filename);

            // Try to open the file
            try {
                java.awt.Desktop.getDesktop().open(new java.io.File(filename));
            } catch (Exception ex) {
                // Ignore if cannot open
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al generar PDF: " + e.getMessage());
        }
    }

    private String getItemName(String type, int id) {
        String name = "Desconocido";
        String sql = "";
        if ("Servicio".equals(type)) {
            sql = "SELECT nombre FROM Servicio WHERE id_servicio = ?";
        } else {
            sql = "SELECT nombre FROM Producto WHERE id_producto = ?";
        }

        try (Connection conn = Conexion.conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("nombre");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name + " (" + type + ")";
    }
}
