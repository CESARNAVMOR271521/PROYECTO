package proyecto.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import proyecto.dao.UsuarioSistemaDAO;
import proyecto.modelo.UsuarioSistema;
import proyecto.util.Theme;

import java.awt.*;

public class RegistroDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPass;
    private JComboBox<String> cmbRol;
    private UsuarioSistemaDAO usuarioDAO;

    public RegistroDialog(JFrame parent) {
        super(parent, "Registro de Usuario", true);
        setBounds(100, 100, 400, 450);
        setLocationRelativeTo(parent);
        usuarioDAO = new UsuarioSistemaDAO();

        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBackground(Theme.COLOR_SECONDARY);
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        // Titulo
        JLabel lblTitulo = new JLabel("NUEVO USUARIO");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setFont(Theme.FONT_TITLE);
        lblTitulo.setForeground(Theme.COLOR_ACCENT_GOLD);
        lblTitulo.setBounds(10, 11, 364, 30);
        contentPanel.add(lblTitulo);

        // Campos
        int y = 50;

        addLabel(contentPanel, "Usuario:", 30, y);
        txtUsuario = new JTextField();
        txtUsuario.setBounds(30, y + 20, 320, 25);
        contentPanel.add(txtUsuario);
        y += 50;

        addLabel(contentPanel, "Contraseña:", 30, y);
        txtPassword = new JPasswordField();
        txtPassword.setBounds(30, y + 20, 320, 25);
        contentPanel.add(txtPassword);
        y += 50;

        addLabel(contentPanel, "Confirmar Contraseña:", 30, y);
        txtConfirmPass = new JPasswordField();
        txtConfirmPass.setBounds(30, y + 20, 320, 25);
        contentPanel.add(txtConfirmPass);
        y += 50;

        addLabel(contentPanel, "Rol:", 30, y);
        cmbRol = new JComboBox<>(new String[] { "empleado", "admin" });
        cmbRol.setBounds(30, y + 20, 320, 25);
        contentPanel.add(cmbRol);

        // Botones
        JPanel buttonPane = new JPanel();
        buttonPane.setBackground(Theme.COLOR_PRIMARY);
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton okButton = Theme.createStyledButton("GUARDAR");
        okButton.addActionListener(e -> registrarUsuario());
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);

        JButton cancelButton = Theme.createStyledButton("CANCELAR");
        cancelButton.addActionListener(e -> dispose());
        buttonPane.add(cancelButton);
    }

    private void addLabel(JPanel panel, String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, 150, 20);
        lbl.setForeground(Theme.COLOR_TEXT);
        lbl.setFont(Theme.FONT_BOLD);
        panel.add(lbl);
    }

    private void registrarUsuario() {
        String usuario = txtUsuario.getText();
        String pass = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirmPass.getPassword());
        String rol = (String) cmbRol.getSelectedItem();

        if (usuario.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden", "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        UsuarioSistema nuevo = new UsuarioSistema();
        nuevo.setNombreUsuario(usuario);
        nuevo.setPassword(pass); // El DAO se encargará del hash
        nuevo.setRol(rol);

        if (usuarioDAO.insertar(nuevo)) {
            JOptionPane.showMessageDialog(this, "Usuario registrado correctamente", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar usuario. Posiblemente el usuario ya existe.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
