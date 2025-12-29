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

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.io.File;

import proyecto.dao.DetalleVentaDAO;
import proyecto.dao.FacturaDAO;
import proyecto.modelo.DetalleVenta;
import proyecto.util.Theme;
import proyecto.util.ModernIcon.IconType;

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

        JButton btnPrint = Theme.createStyledButton("Imprimir PDF", IconType.PRINT);
        btnPrint.addActionListener(e -> printSelectedFactura());

        JButton btnRefresh = Theme.createStyledButton("Actualizar", IconType.REFRESH);
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
        
        Theme.bindActionKeys(this, btnRefresh, btnPrint, null, null);
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

            // Colors
            BaseColor colorPrimary = new BaseColor(21, 40, 64);   // Navy
            BaseColor colorGold = new BaseColor(212, 175, 55);    // Gold

            // Fonts
            Font fontTitle = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, colorPrimary);
            Font fontSubtitle = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.DARK_GRAY);
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, colorGold);
            Font fontNormal = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);

            // --- HEADER (Table with 2 columns: Logo | Info) ---
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 2}); // Logo takes 1 part, Text takes 2 parts

            // Cell 1: Logo
            PdfPCell cellLogo = new PdfPCell();
            cellLogo.setBorder(PdfPCell.NO_BORDER);
            try {
                com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance("src/img/logo.jpg");
                logo.scaleToFit(100, 100);
                logo.setAlignment(Element.ALIGN_LEFT);
                cellLogo.addElement(logo);
            } catch (Exception e) {
                cellLogo.addElement(new Paragraph("CHUPIRULES", fontTitle));
            }
            headerTable.addCell(cellLogo);

            // Cell 2: Company Info
            PdfPCell cellInfo = new PdfPCell();
            cellInfo.setBorder(PdfPCell.NO_BORDER);
            cellInfo.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellInfo.addElement(new Paragraph("BARBERÍA CHUPIRULES", fontTitle));
            cellInfo.addElement(new Paragraph("Dirección: Calle Falsa 123, Ciudad", fontSubtitle));
            cellInfo.addElement(new Paragraph("Tel: 555-123-4567", fontSubtitle));
            cellInfo.addElement(new Paragraph("RFC: XAXX010101000", fontSubtitle));
            headerTable.addCell(cellInfo);

            document.add(headerTable);
            document.add(new Paragraph(" ")); // Spacer
            
            // --- INVOICE DETAILS ---
            Paragraph pDetails = new Paragraph("Detalles de la Factura", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, colorPrimary));
            pDetails.setSpacingAfter(10);
            document.add(pDetails);

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            infoTable.addCell(new Paragraph("Folio: " + idFactura, fontNormal));
            infoTable.addCell(new Paragraph("Fecha: " + fecha, fontNormal));
            infoTable.addCell(new Paragraph("Cliente: " + cliente, fontNormal));
            infoTable.addCell(new Paragraph(" ", fontNormal));
            document.add(infoTable);
            document.add(new Paragraph(" "));

            // --- ITEMS TABLE ---
            PdfPTable pdfTable = new PdfPTable(4);
            pdfTable.setWidthPercentage(100);
            pdfTable.setSpacingBefore(10f);
            pdfTable.setSpacingAfter(10f);
            pdfTable.setWidths(new float[]{1, 4, 2, 2}); // Relative widths

            // Headers
            String[] headers = {"Cant.", "Descripción", "Precio Unit.", "Subtotal"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Paragraph(h, fontHeader));
                cell.setBackgroundColor(colorPrimary);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(8);
                cell.setBorderColor(colorGold);
                pdfTable.addCell(cell);
            }

            // Data
            List<DetalleVenta> detalles = detalleDAO.listarPorVenta(idVenta);
            for (DetalleVenta d : detalles) {
                String itemName = getItemName(d.getTipoItem(), d.getIdItem());

                PdfPCell c1 = new PdfPCell(new Paragraph(String.valueOf(d.getCantidad()), fontNormal));
                PdfPCell c2 = new PdfPCell(new Paragraph(itemName, fontNormal));
                PdfPCell c3 = new PdfPCell(new Paragraph(String.format("$%.2f", d.getPrecioUnitario()), fontNormal));
                PdfPCell c4 = new PdfPCell(new Paragraph(String.format("$%.2f", d.getSubtotal()), fontNormal));

                c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                c4.setHorizontalAlignment(Element.ALIGN_RIGHT);

                // Padding
                c1.setPadding(5); c2.setPadding(5); c3.setPadding(5); c4.setPadding(5);

                pdfTable.addCell(c1);
                pdfTable.addCell(c2);
                pdfTable.addCell(c3);
                pdfTable.addCell(c4);
            }

            document.add(pdfTable);

            // --- TOTAL ---
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(40);
            totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            
            PdfPCell lblTotal = new PdfPCell(new Paragraph("TOTAL:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, colorPrimary)));
            lblTotal.setBorder(PdfPCell.NO_BORDER);
            lblTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            
            PdfPCell valTotal = new PdfPCell(new Paragraph("$" + String.format("%.2f", total), new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.RED)));
            valTotal.setBorder(PdfPCell.BOTTOM);
            valTotal.setBorderColor(colorGold);
            valTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            valTotal.setPaddingBottom(5);

            totalTable.addCell(lblTotal);
            totalTable.addCell(valTotal);
            document.add(totalTable);

            // --- FOOTER ---
            document.add(new Paragraph(" "));
            Paragraph footer = new Paragraph("¡Gracias por su preferencia!", new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            
            // Auto-open
            try {
                File file = new File(filename);
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                } else {
                    JOptionPane.showMessageDialog(this, "PDF generado: " + file.getAbsolutePath() + "\n(El sistema no soporta apertura automática)");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "PDF Generado pero error al abrir: " + ex.getMessage());
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
