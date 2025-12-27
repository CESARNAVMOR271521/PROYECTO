package proyecto.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Utility class for the "Barber Chupirul" Design System.
 * Colors extracted from the brand logo.
 */
public class Theme {

    // 🎨 PALETA DE COLORES BARBER CHUPIRUL
    public static final Color COLOR_PRIMARY = new Color(21, 40, 64);      // Azul Marino Profundo (Fondo principal)
    public static final Color COLOR_SECONDARY = new Color(30, 53, 78);    // Azul Marino más claro (Paneles, Sidebar)
    public static final Color COLOR_ACCENT_RED = new Color(200, 32, 47);  // Rojo Dulce (Botones, detalles)
    public static final Color COLOR_ACCENT_GOLD = new Color(212, 175, 55); // Dorado (Bordes, Textos destacados)
    public static final Color COLOR_TEXT = new Color(240, 234, 214);      // Crema/Beige (Texto principal)
    public static final Color COLOR_TEXT_DARK = new Color(20, 20, 20);    // Texto oscuro para fondos claros (si los hay)
    
    public static final Font FONT_TITLE = new Font("Serif", Font.BOLD, 24);
    public static final Font FONT_REGULAR = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 14);

    /**
     * Aplica el estilo base a un componente (Fondo y Texto).
     */
    public static void applyTheme(JComponent component) {
        component.setBackground(COLOR_PRIMARY);
        component.setForeground(COLOR_TEXT);
    }

    /**
     * Crea un botón estilizado con el tema.
     */
    public static JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE); // Texto blanco
        btn.setBackground(COLOR_ACCENT_RED);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(COLOR_ACCENT_GOLD, 2));
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        // Hover Effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(COLOR_ACCENT_RED.brighter());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(COLOR_ACCENT_RED);
                }
            }
        });

        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE); // Texto blanco
        btn.setBackground(COLOR_SECONDARY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(COLOR_ACCENT_GOLD, 2));
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        // Hover Effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(COLOR_SECONDARY.brighter());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(COLOR_SECONDARY);
                }
            }
        });

        return btn;
    }

    /**
     * Aplica estilo a una JTable y su Header.
     */
    public static void styleTable(JTable table) {
        // Header
        JTableHeader header = table.getTableHeader();
        header.setBackground(COLOR_ACCENT_GOLD);
        header.setForeground(COLOR_PRIMARY);
        header.setFont(FONT_BOLD);
        header.setOpaque(true);

        // Body
        table.setBackground(COLOR_SECONDARY);
        table.setForeground(COLOR_TEXT);
        table.setGridColor(COLOR_ACCENT_GOLD);
        table.setFont(FONT_REGULAR);
        table.setRowHeight(25);
        table.setSelectionBackground(COLOR_ACCENT_RED);
        table.setSelectionForeground(COLOR_TEXT);

        // Centrar celdas (opcional, pero se ve mejor)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(COLOR_SECONDARY);
        centerRenderer.setForeground(COLOR_TEXT);
        
        try {
            // Intentar aplicar a todas las columnas si es posible
            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        } catch (Exception e) {
            // Ignorar si falla al configurar renderers específicos
        }
    }
}
