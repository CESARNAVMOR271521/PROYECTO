package proyecto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import proyecto.util.Config;
import proyecto.util.Theme;

public class ConfiguracionPanel extends JPanel {

    private JTextField txtNombre;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JTextField txtMensaje;
    
    // Theme Colors
    private Color tempPrimary;
    private Color tempSecondary;
    private JPanel previewPanel;

    public ConfiguracionPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.COLOR_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel lblTitle = new JLabel("🔧 Configuración del Sistema");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.COLOR_ACCENT_GOLD);
        add(lblTitle, BorderLayout.NORTH);

        // Main Content - Scrollable
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Theme.COLOR_PRIMARY);

        // 1. Datos del Negocio
        addSectionHeader(content, "🏢 Datos del Negocio");
        
        JPanel businessPanel = new JPanel(new GridLayout(0, 2, 20, 15));
        businessPanel.setBackground(Theme.COLOR_PRIMARY);
        businessPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        businessPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtNombre = addField(businessPanel, "Nombre del Negocio:", Config.get("nombre_negocio", "CHUPIRULES"));
        txtDireccion = addField(businessPanel, "Dirección:", Config.get("direccion"));
        txtTelefono = addField(businessPanel, "Teléfono:", Config.get("telefono"));
        txtMensaje = addField(businessPanel, "Mensaje Ticket:", Config.get("mensaje_ticket"));
        
        content.add(businessPanel);
        content.add(Box.createVerticalStrut(20));

        // 2. Base de Datos
        addSectionHeader(content, "💾 Base de Datos");
        
        JPanel dbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        dbPanel.setBackground(Theme.COLOR_PRIMARY);
        dbPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton btnFullBackup = Theme.createStyledButton("Crear Respaldo (Backup)", proyecto.util.ModernIcon.IconType.SAVE);
        btnFullBackup.addActionListener(e -> crearRespaldo());
        
        JButton btnRestore = Theme.createStyledButton("Restaurar Base de Datos", proyecto.util.ModernIcon.IconType.REFRESH);
        btnRestore.setBackground(Theme.COLOR_ACCENT_RED);
        btnRestore.addActionListener(e -> restaurarBaseDatos());
        
        JButton btnTest = Theme.createSecondaryButton("Probar Conexión");
        btnTest.addActionListener(e -> probarConexion());

        dbPanel.add(btnTest);
        dbPanel.add(btnFullBackup);
        dbPanel.add(btnRestore);
        
        content.add(dbPanel);
        content.add(Box.createVerticalStrut(20));

        // 3. Personalización
        addSectionHeader(content, "🎨 Personalización");
        
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        colorPanel.setBackground(Theme.COLOR_PRIMARY);
        colorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Initial colors
        tempPrimary = Theme.COLOR_PRIMARY;
        tempSecondary = Theme.COLOR_SECONDARY;
        
        colorPanel.add(createColorPicker("Color Fondo Principal", tempPrimary, c -> tempPrimary = c));
        colorPanel.add(createColorPicker("Color Menú Lateral", tempSecondary, c -> tempSecondary = c));
        
        // Preview Box
        previewPanel = new JPanel();
        previewPanel.setPreferredSize(new java.awt.Dimension(100, 50));
        previewPanel.setBackground(tempPrimary);
        previewPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        
        content.add(colorPanel);
        content.add(Box.createVerticalStrut(30));

        // Save Button (Global)
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(Theme.COLOR_PRIMARY);
        
        JButton btnSave = Theme.createStyledButton("Guardar Todo");
        btnSave.setPreferredSize(new java.awt.Dimension(200, 50));
        btnSave.addActionListener(e -> guardarTodo());
        
        footer.add(btnSave);
        
        // Final Assembly
        content.add(footer);
        
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private void addSectionHeader(JPanel container, String title) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(Theme.COLOR_TEXT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
        container.add(lbl);
        
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new java.awt.Dimension(Short.MAX_VALUE, 10));
        sep.setBackground(Theme.COLOR_TEXT_MUTED);
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(sep);
        container.add(Box.createVerticalStrut(10));
    }

    private JTextField addField(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_BOLD);
        lbl.setForeground(Theme.COLOR_TEXT);
        panel.add(lbl);

        JTextField txt = new JTextField(value);
        Theme.styleTextField(txt);
        panel.add(txt);
        return txt;
    }
    
    private JPanel createColorPicker(String title, Color initial, java.util.function.Consumer<Color> onSelect) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(Theme.COLOR_PRIMARY);
        
        JLabel lbl = new JLabel(title);
        lbl.setForeground(Theme.COLOR_TEXT);
        p.add(lbl, BorderLayout.NORTH);
        
        JButton btn = new JButton();
        btn.setPreferredSize(new java.awt.Dimension(120, 40));
        btn.setBackground(initial);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        btn.setFocusPainted(false);
        
        btn.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Seleccionar " + title, btn.getBackground());
            if (newColor != null) {
                btn.setBackground(newColor);
                onSelect.accept(newColor);
            }
        });
        
        p.add(btn, BorderLayout.CENTER);
        return p;
    }

    // --- ACTIONS ---

    private void probarConexion() {
        try (Connection conn = DatabaseHelper.connect()) {
            if (conn != null && !conn.isClosed()) {
                javax.swing.JOptionPane.showMessageDialog(this, "✅ Conexión Exitosa a la Base de Datos.");
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "❌ Error de Conexión.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
             javax.swing.JOptionPane.showMessageDialog(this, "❌ Excepción: " + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void crearRespaldo() {
        // Mock implementation relying on file copy
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Guardar Respaldo");
        fc.setSelectedFile(new File("barberia_backup.db"));
        
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File dest = fc.getSelectedFile();
            // In a real app we'd copy the SQLite file. 
            // For now, just a message or mock copy.
             javax.swing.JOptionPane.showMessageDialog(this, "Respaldo guardado en: " + dest.getAbsolutePath() + "\n(Función simulada)");
        }
    }

    private void restaurarBaseDatos() {
        int confirm = javax.swing.JOptionPane.showConfirmDialog(this, 
            "⚠️ ¿Estás seguro? Esto reemplazará los datos actuales.",
            "Restaurar Base de Datos", javax.swing.JOptionPane.YES_NO_OPTION);
            
        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                javax.swing.JOptionPane.showMessageDialog(this, "Base de datos restaurada correctamente.\n(Función simulada)");
            }
        }
    }

    private void guardarTodo() {
        // Save Business Data
        Config.set("nombre_negocio", txtNombre.getText().trim());
        Config.set("direccion", txtDireccion.getText().trim());
        Config.set("telefono", txtTelefono.getText().trim());
        Config.set("mensaje_ticket", txtMensaje.getText().trim());
        
        // Save Colors (We would need to persist these in Config and have Theme load them)
        Config.set("color_primary_r", String.valueOf(tempPrimary.getRed()));
        Config.set("color_primary_g", String.valueOf(tempPrimary.getGreen()));
        Config.set("color_primary_b", String.valueOf(tempPrimary.getBlue()));
        
        Config.set("color_secondary_r", String.valueOf(tempSecondary.getRed()));
        Config.set("color_secondary_g", String.valueOf(tempSecondary.getGreen()));
        Config.set("color_secondary_b", String.valueOf(tempSecondary.getBlue()));

        javax.swing.JOptionPane.showMessageDialog(this, "Configuración Guardada.\nReinicia la aplicación para ver los cambios de color completos.", "Guardado", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
}
