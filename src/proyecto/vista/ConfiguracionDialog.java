package proyecto.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import proyecto.util.Config;
import proyecto.util.Theme;

public class ConfiguracionDialog extends JDialog {

    private JTextField txtNombre;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JTextField txtMensaje;
    private Runnable onSave;

    public ConfiguracionDialog(Frame parent, Runnable onSave) {
        super(parent, "Configuración del Sistema", true);
        this.onSave = onSave;
        
        setSize(450, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.COLOR_PRIMARY);

        // Header
        JLabel lblTitle = new JLabel("Ajustes del Negocio");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.COLOR_ACCENT_GOLD);
        lblTitle.setHorizontalAlignment(JLabel.CENTER);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        formPanel.setBackground(Theme.COLOR_PRIMARY);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        txtNombre = addField(formPanel, "Nombre del Negocio:", Config.get("nombre_negocio", "CHUPIRULES"));
        txtDireccion = addField(formPanel, "Dirección:", Config.get("direccion"));
        txtTelefono = addField(formPanel, "Teléfono:", Config.get("telefono"));
        txtMensaje = addField(formPanel, "Mensaje Ticket:", Config.get("mensaje_ticket"));

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        btnPanel.setBackground(Theme.COLOR_PRIMARY);

        JButton btnSave = Theme.createStyledButton("Guardar Configuración");
        btnSave.addActionListener(e -> guardar());
        
        JButton btnCancel = Theme.createSecondaryButton("Cancelar");
        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        add(btnPanel, BorderLayout.SOUTH);
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

    private void guardar() {
        Config.set("nombre_negocio", txtNombre.getText().trim());
        Config.set("direccion", txtDireccion.getText().trim());
        Config.set("telefono", txtTelefono.getText().trim());
        Config.set("mensaje_ticket", txtMensaje.getText().trim());
        
        javax.swing.JOptionPane.showMessageDialog(this, "Configuración guardada correctamente.");
        if (onSave != null) onSave.run();
        dispose();
    }
}
