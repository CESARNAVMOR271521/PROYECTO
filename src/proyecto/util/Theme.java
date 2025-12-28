package proyecto.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Utility class for the "Barber Chupirul" Design System.
 * Refactored for a friendlier, simpler, and more modern interface.
 */
public class Theme {

    // 🎨 PALETA DE COLORES BARBER CHUPIRUL (Respetando Logo)
    public static final Color COLOR_PRIMARY = new Color(21, 40, 64);      // Azul Marino Profundo
    public static final Color COLOR_SECONDARY = new Color(30, 53, 78);    // Azul Marino más claro
    
    // Accents
    public static final Color COLOR_ACCENT_RED = new Color(200, 32, 47);  // Rojo Dulce
    public static final Color COLOR_ACCENT_GOLD = new Color(212, 175, 55); // Dorado Imperial
    public static final Color COLOR_ACCENT_BLUE = new Color(52, 152, 219); // Action Blue (Auxiliar)
    
    // Text
    public static final Color COLOR_TEXT = new Color(240, 234, 214);      // Crema/Beige (Texto principal)
    public static final Color COLOR_TEXT_MUTED = new Color(180, 180, 180); // Gris claro
    public static final Color COLOR_TEXT_DARK = new Color(20, 20, 20);
    
    // Inputs (Adjusted for dark theme)
    public static final Color COLOR_INPUT_BG = new Color(245, 245, 245);
    public static final Color COLOR_INPUT_TEXT = new Color(20, 20, 20);

    // Fonts - Using widely available modern sans-serifs
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 16);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    // Dimensions
    public static final int ROUND_RADIUS = 15;

    /**
     * Initializes global UI defaults. 
     * Call this at the very start of the application.
     */
    public static void setupUI() {
        try {
            // Set global font
            UIManager.put("Label.font", FONT_REGULAR);
            UIManager.put("Button.font", FONT_BOLD);
            UIManager.put("TextField.font", FONT_REGULAR);
            UIManager.put("TextArea.font", FONT_REGULAR);
            UIManager.put("Table.font", FONT_REGULAR);
            UIManager.put("TableHeader.font", FONT_BOLD);
            
            // Adjust scrollbars to be less intrusive (if supported by LAF)
            UIManager.put("ScrollBar.width", 10);
            
            // Tooltips
            UIManager.put("ToolTip.background", COLOR_SECONDARY);
            UIManager.put("ToolTip.foreground", COLOR_TEXT);
            UIManager.put("ToolTip.border", BorderFactory.createLineBorder(COLOR_ACCENT_GOLD));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Applies the theme recursively to a container and all its children.
     */
    public static void applyRecursive(Container container) {
        if (container instanceof JPanel) {
            if (container.getBackground() == null || 
                (container.getBackground().getRed() == 238 && container.getBackground().getGreen() == 238)) {
                // Only override default swing grey, respect manually set colors if they are intentional
                container.setBackground(COLOR_PRIMARY);
            }
        }
        
        for (Component c : container.getComponents()) {
            if (c instanceof JPanel) {
                // Panels default to primary unless already styled
                if (c.getName() != null && c.getName().contains("secondary")) {
                    c.setBackground(COLOR_SECONDARY);
                } else if (c.getBackground() != null && c.getBackground().equals(UIManager.getColor("Panel.background"))) {
                    c.setBackground(COLOR_PRIMARY); 
                }
                applyRecursive((Container) c);
            } else if (c instanceof JTextField) {
                styleTextField((JTextField) c);
            } else if (c instanceof JButton) {
               // Verify if it's not a voice button or already styled custom button
               // Avoid explicit dependency on project package
               if (!c.getClass().getSimpleName().equals("VoiceButton")) { 
                   if (!c.getFont().equals(FONT_BOLD)) {
                       c.setFont(FONT_BOLD);
                   }
               }
            } else if (c instanceof JTable) {
                styleTable((JTable) c);
            } else if (c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                if (lbl.getForeground().equals(Color.BLACK) || lbl.getForeground().equals(UIManager.getColor("Label.foreground"))) {
                     lbl.setForeground(COLOR_TEXT);
                }
                // Heuristic: if font size is large, keep it (title), else ensure regular
                if (lbl.getFont().getSize() < 16) {
                    lbl.setFont(FONT_REGULAR);
                } else if (!lbl.getFont().isBold()) {
                    lbl.setFont(FONT_TITLE);
                }
            } else if (c instanceof JScrollPane) {
                c.setBackground(COLOR_PRIMARY);
                ((JScrollPane) c).getViewport().setBackground(COLOR_SECONDARY);
                applyRecursive(((JScrollPane) c).getViewport());
            } else if (c instanceof Container) {
                applyRecursive((Container) c);
            }
        }
    }

    public static void applyTheme(JComponent component) {
        component.setBackground(COLOR_PRIMARY);
        component.setForeground(COLOR_TEXT);
        applyRecursive(component); // Use recursive by default now
    }

    /**
     * Creates a modern, rounded button.
     */
    public static JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(COLOR_ACCENT_RED);
        btn.setBorder(new RoundedBorder(ROUND_RADIUS));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        // Add dynamic behavior
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                 if(btn.isEnabled()) btn.setBackground(COLOR_ACCENT_RED.brighter());
                 btn.repaint();
            }
            public void mouseExited(MouseEvent e) {
                 if(btn.isEnabled()) btn.setBackground(COLOR_ACCENT_RED);
                 btn.repaint();
            }
        });
        
        // Custom painting for rounded background
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), ROUND_RADIUS, ROUND_RADIUS);
                
                // Draw text and icon via super
                super.paint(g2, c);
                g2.dispose();
            }
        });

        return btn;
    }
    
    // New secondary button (grey/blue)
    public static JButton createSecondaryButton(String text) {
        JButton btn = createStyledButton(text);
        btn.setBackground(COLOR_SECONDARY.brighter());
        btn.setForeground(COLOR_TEXT);
        
        btn.addMouseListener(new MouseAdapter() {
             public void mouseEntered(MouseEvent e) {
                 if(btn.isEnabled()) btn.setBackground(COLOR_SECONDARY.brighter().brighter());
                 btn.repaint();
            }
            public void mouseExited(MouseEvent e) {
                 if(btn.isEnabled()) btn.setBackground(COLOR_SECONDARY.brighter());
                 btn.repaint();
            }
        });
        return btn;
    }

    public static void styleTextField(JTextField txt) {
        txt.setFont(FONT_REGULAR);
        txt.setForeground(COLOR_INPUT_TEXT);
        txt.setBackground(COLOR_INPUT_BG);
        txt.setCaretColor(COLOR_INPUT_TEXT);
        txt.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10, COLOR_TEXT_MUTED),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Focus effect
        txt.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                txt.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(10, COLOR_ACCENT_GOLD), // Highlight border
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
            public void focusLost(FocusEvent e) {
                txt.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(10, COLOR_TEXT_MUTED),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
        });
    }

    public static void styleTable(JTable table) {
        // Header
        JTableHeader header = table.getTableHeader();
        header.setBackground(COLOR_SECONDARY);
        header.setForeground(COLOR_ACCENT_GOLD);
        header.setFont(FONT_BOLD);
        header.setOpaque(true);
        // Remove border for cleaner look
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_ACCENT_GOLD));

        // Body
        table.setBackground(new Color(245, 245, 245)); // Light bg for rows for readability
        table.setForeground(COLOR_TEXT_DARK);          // Dark text
        table.setGridColor(new Color(220, 220, 220));
        table.setFont(FONT_REGULAR);
        table.setRowHeight(30); // More breathing room
        table.setSelectionBackground(COLOR_ACCENT_BLUE);
        table.setSelectionForeground(Color.WHITE);
        table.setShowVerticalLines(false); // Only horizontal lines look cleaner

        // Alternating row colors could be done via custom renderer, but basic styling first
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        try {
            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        } catch (Exception e) {}
    }
    
    // Helper class for rounded borders
    public static class RoundedBorder extends AbstractBorder {
        private int radius;
        private Color color;

        public RoundedBorder(int radius) {
            this.radius = radius;
            this.color = null; // No border line, just shape if null
        }
        
        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (color != null) {
                g2.setColor(color);
                g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            }
        }

        @Override
        public Insets getBorderInsets(Component c) {
             // Add enough padding so text doesn't touch rounded corners
            return new Insets(this.radius / 3 + 2, this.radius / 3 + 5, this.radius / 3 + 2, this.radius / 3 + 5);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    /**
     * Binds standard function key modifiers to action buttons in a panel.
     * Shift+F1 -> Action 1 (Agregar)
     * Shift+F2 -> Action 2 (Actualizar)
     * Shift+F3 -> Action 3 (Eliminar)
     * Shift+F4 -> Action 4 (Limpiar)
     */
    public static void bindActionKeys(JComponent panel, JButton btn1, JButton btn2, JButton btn3, JButton btn4) {
        javax.swing.InputMap inputMap = panel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        javax.swing.ActionMap actionMap = panel.getActionMap();

        if (btn1 != null) {
            inputMap.put(javax.swing.KeyStroke.getKeyStroke("shift F1"), "action1");
            actionMap.put("action1", new javax.swing.AbstractAction() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    btn1.doClick();
                }
            });
            btn1.setToolTipText(btn1.getText() + " (Shift+F1)");
        }

        if (btn2 != null) {
            inputMap.put(javax.swing.KeyStroke.getKeyStroke("shift F2"), "action2");
            actionMap.put("action2", new javax.swing.AbstractAction() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    btn2.doClick();
                }
            });
            btn2.setToolTipText(btn2.getText() + " (Shift+F2)");
        }

        if (btn3 != null) {
            inputMap.put(javax.swing.KeyStroke.getKeyStroke("shift F3"), "action3");
            actionMap.put("action3", new javax.swing.AbstractAction() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    btn3.doClick();
                }
            });
            btn3.setToolTipText(btn3.getText() + " (Shift+F3)");
        }

        if (btn4 != null) {
            inputMap.put(javax.swing.KeyStroke.getKeyStroke("shift F4"), "action4");
            actionMap.put("action4", new javax.swing.AbstractAction() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    btn4.doClick();
                }
            });
            btn4.setToolTipText(btn4.getText() + " (Shift+F4)");
        }
    }
}
