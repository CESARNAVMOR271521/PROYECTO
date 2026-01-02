package proyecto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import proyecto.util.Theme;

public class DashboardPanel extends JPanel {

    private java.util.function.Consumer<String> navigation;

    public DashboardPanel() {
        this(null);
    }

    public DashboardPanel(java.util.function.Consumer<String> navigation) {
        this.navigation = navigation;
        setLayout(new BorderLayout(20, 20));
        Theme.applyTheme(this);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel lblTitle = new JLabel("Resumen del Negocio");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.COLOR_ACCENT_GOLD);
        lblTitle.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Header Panel (Title + Actions)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.COLOR_PRIMARY);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        // Actions Panel
        JPanel actionsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        actionsPanel.setBackground(Theme.COLOR_PRIMARY);
        
        if (navigation != null) {
            actionsPanel.add(createActionButton("💰 Nueva Venta", "VENTAS", Theme.COLOR_ACCENT_BLUE));
            actionsPanel.add(createActionButton("📅 Nueva Cita", "CITAS", Theme.COLOR_ACCENT_RED));
            actionsPanel.add(createActionButton("👥 Nuevo Cliente", "CLIENTES", Theme.COLOR_ACCENT_GOLD));
        }
        
        // Wrap actions in a flow/box to not stretch too much? 
        // actually GridLayout is fine, buttons will be big.
        // Let's make them fixed size?
        JPanel actionWrapper = new JPanel();
        actionWrapper.setBackground(Theme.COLOR_PRIMARY);
        actionWrapper.add(actionsPanel);
        
        // headerPanel.add(actionWrapper, BorderLayout.EAST); 
        // Better: Put actions in the main flow 

        // Content Scroll
        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBackground(Theme.COLOR_PRIMARY);
        
        // Container for Top stuff
        JPanel topContainer = new JPanel(new BorderLayout(0, 20));
        topContainer.setBackground(Theme.COLOR_PRIMARY);
        topContainer.add(lblTitle, BorderLayout.NORTH);
        
        if (navigation != null) {
             topContainer.add(createQuickActionsPanel(), BorderLayout.CENTER);
        }

        // 1. KPI Cards Row
        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        kpiPanel.setBackground(Theme.COLOR_PRIMARY);
        kpiPanel.setPreferredSize(new Dimension(0, 120));

        kpiPanel.add(createKpiCard("Ventas Hoy", getVentasHoy(), Theme.COLOR_ACCENT_BLUE));
        kpiPanel.add(createKpiCard("Citas Hoy", getCitasHoy(), Theme.COLOR_ACCENT_RED));
        kpiPanel.add(createKpiCard("Total Clientes", getTotalClientes(), Theme.COLOR_ACCENT_GOLD));
        kpiPanel.add(createKpiCard("Prod. Bajo Stock", getProductosBajoStock(), Color.ORANGE));

        // Group TopContainer and KPI
        JPanel northGroup = new JPanel(new BorderLayout(0, 20));
        northGroup.setBackground(Theme.COLOR_PRIMARY);
        northGroup.add(topContainer, BorderLayout.NORTH);
        northGroup.add(kpiPanel, BorderLayout.CENTER);

        content.add(northGroup, BorderLayout.NORTH);

        // 2. Charts Row
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        chartsPanel.setBackground(Theme.COLOR_PRIMARY);

        // Chart 1: Sales Distribution (Service vs Product)
        chartsPanel.add(createChartCard("Distribución de Ingresos", getDistribucionIngresos()));

        // Chart 2: Top Selling Products/Services or Payment Methods
        chartsPanel.add(createChartCard("Ventas por Método de Pago", getMetodosPago()));

        content.add(chartsPanel, BorderLayout.CENTER);

        // ScrollPane in case screen is small
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Theme.COLOR_PRIMARY);
        // Increase scroll speed
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }
    
    private JPanel createQuickActionsPanel() {
        JPanel p = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        p.setBackground(Theme.COLOR_PRIMARY);
        
        p.add(createActionButton("💰 Registrar Venta", "VENTAS", Theme.COLOR_ACCENT_BLUE));
        p.add(javax.swing.Box.createHorizontalStrut(15));
        p.add(createActionButton("📅 Agendar Cita", "CITAS", Theme.COLOR_ACCENT_RED));
        p.add(javax.swing.Box.createHorizontalStrut(15));
        p.add(createActionButton("👥 Registrar Cliente", "CLIENTES", Theme.COLOR_ACCENT_GOLD));
        
        return p;
    }
    
    private javax.swing.JButton createActionButton(String text, String module, Color color) {
        javax.swing.JButton btn = new javax.swing.JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Paint custom rounded background (Black)
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                
                // Paint White Border
                g2.setColor(Color.WHITE);
                g2.setStroke(new java.awt.BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                
                // Accent Indicator (Little strip on left to keep the color coding)
                g2.setColor(color);
                g2.fillRect(4, 10, 4, getHeight()-20);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(Color.BLACK); // Explicit Black base
        // btn.setBorder(new Theme.RoundedBorder(15, color)); // Replaced by custom paint
        btn.setFocusPainted(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 60)); // Slightly larger
        
        // Critical for custom painting transparency
        btn.setContentAreaFilled(false); 
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0)); // Padding for text to clear indicator

        btn.addActionListener(e -> {
            if (navigation != null) navigation.accept(module);
        });
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                 btn.setBackground(new Color(40, 40, 40)); // Dark Gray on hover
                 btn.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.BLACK); // Back to black
                btn.repaint();
            }
        });
        
        return btn;
    }

    private JPanel createKpiCard(String title, String value, Color accentInfo) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.COLOR_SECONDARY);
        card.setBorder(BorderFactory.createCompoundBorder(
            new Theme.RoundedBorder(15, accentInfo), 
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblVal.setForeground(Theme.COLOR_TEXT);
        lblVal.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(Theme.FONT_BOLD);
        lblTitle.setForeground(Theme.COLOR_TEXT_MUTED);

        card.add(lblVal, BorderLayout.CENTER);
        card.add(lblTitle, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createChartCard(String title, Map<String, Double> data) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.COLOR_SECONDARY);
        card.setBorder(new Theme.RoundedBorder(15));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(Theme.FONT_BOLD);
        lblTitle.setForeground(Theme.COLOR_ACCENT_GOLD);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        card.add(lblTitle, BorderLayout.NORTH);

        SimplePieChart chart = new SimplePieChart(data);
        card.add(chart, BorderLayout.CENTER);

        return card;
    }

    // --- DATA FETCHING (Mocked or Real SQL) ---

    private String getVentasHoy() {
        String sql = "SELECT SUM(total) FROM Venta WHERE date(fecha) = date('now', 'localtime')";
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                double total = rs.getDouble(1);
                return String.format("$%.2f", total);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "$0.00";
    }

    private String getCitasHoy() {
        String sql = "SELECT COUNT(*) FROM Cita WHERE date(fecha) = date('now', 'localtime')";
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return String.valueOf(rs.getInt(1));
        } catch (Exception e) { e.printStackTrace(); }
        return "0";
    }

    private String getTotalClientes() {
        String sql = "SELECT COUNT(*) FROM Cliente";
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return String.valueOf(rs.getInt(1));
        } catch (Exception e) { e.printStackTrace(); }
        return "0";
    }

    private String getProductosBajoStock() {
        // Updated to query stock from Producto table
        String sql = "SELECT COUNT(*) FROM Producto WHERE cantidad_actual <= minimo";
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return String.valueOf(rs.getInt(1));
        } catch (Exception e) { e.printStackTrace(); }
        return "0";
    }

    private Map<String, Double> getDistribucionIngresos() {
        Map<String, Double> map = new HashMap<>();
        // Query to split sales by type (Producto vs Servicio) based on DetalleVenta
        // Table DetalleVenta has id_servicio and id_producto nullable columns
        String sql = "SELECT CASE WHEN id_servicio IS NOT NULL THEN 'SERVICIO' ELSE 'PRODUCTO' END as tipo, " +
                     "SUM(cantidad * precio_unitario) as total " +
                     "FROM DetalleVenta " +
                     "GROUP BY tipo";
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString(1).toUpperCase(), rs.getDouble(2));
            }
        } catch (Exception e) { e.printStackTrace(); }
        if (map.isEmpty()) {
            map.put("SIN DATOS", 1.0);
        }
        return map;
    }

    private Map<String, Double> getMetodosPago() {
        Map<String, Double> map = new HashMap<>();
        String sql = "SELECT forma_pago, SUM(monto) FROM Pago GROUP BY forma_pago";
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String metodo = rs.getString(1);
                if (metodo == null) metodo = "Desconocido";
                map.put(metodo.toUpperCase(), rs.getDouble(2));
            }
        } catch (Exception e) { e.printStackTrace(); }
        if (map.isEmpty()) {
            map.put("SIN DATOS", 1.0);
        }
        return map;
    }

    // --- INNER CLASS: PIE CHART ---
    
    private static class SimplePieChart extends JPanel {
        private Map<String, Double> data;
        private Color[] colors = {
            Theme.COLOR_ACCENT_BLUE, Theme.COLOR_ACCENT_RED, Theme.COLOR_ACCENT_GOLD,
            Color.ORANGE, Color.MAGENTA, Color.CYAN
        };

        public SimplePieChart(Map<String, Double> data) {
            this.data = data;
            setBackground(Theme.COLOR_SECONDARY);
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
            
            int width = getWidth();
            int height = getHeight();
            int minDim = Math.min(width, height);
            int diameter = (int) (minDim * 0.7);
            int x = (width - diameter) / 2;
            int y = (height - diameter) / 2;

            double currentAngle = 90;
            int colorIndex = 0;

            // Draw Slices
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                double value = entry.getValue();
                double angle = (value / total) * 360;

                g2.setColor(colors[colorIndex % colors.length]);
                g2.fill(new Arc2D.Double(x, y, diameter, diameter, currentAngle, -angle, Arc2D.PIE));

                currentAngle -= angle;
                colorIndex++;
            }

            // Draw Legend (Simple text overlay or side list - putting simple legend at bottom for now)
            int legendY = height - 30;
            int legendX = 20;
            colorIndex = 0;
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                g2.setColor(colors[colorIndex % colors.length]);
                g2.fillRect(legendX, legendY, 10, 10);
                
                g2.setColor(Theme.COLOR_TEXT);
                String label = String.format("%s (%.0f%%)", entry.getKey(), (entry.getValue()/total)*100);
                g2.drawString(label, legendX + 15, legendY + 9);

                legendX += 130; // Shift for next item
                if (legendX > width - 100) {
                    legendX = 20;
                    legendY += 15;
                }
                colorIndex++;
            }
        }
    }
}
