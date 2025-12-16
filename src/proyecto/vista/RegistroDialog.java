package proyecto.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import proyecto.dao.UsuarioSistemaDAO;
import proyecto.modelo.UsuarioSistema;
import java.awt.*;

public class RegistroDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextField txtNombre;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPass;
    private JComboBox<String> cmbRol;
    private UsuarioSistemaDAO usuarioDAO;

    // Colores del tema
    private final Color BG_MARBLE = new Color(233, 227, 200);
    private final Color BG_GOLD_DARK = new Color(140, 112, 60);
    private final Color BTN_DEFAULT = new Color(199, 179, 106);
    private final Color TXT_MAIN = new Color(60, 45, 20);

    public RegistroDialog(JFrame parent) {
        super(parent, "Registro de Usuario", true);
        setBounds(100, 100, 400, 450);
        setLocationRelativeTo(parent);
        usuarioDAO = new UsuarioSistemaDAO();

        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBackground(BG_MARBLE);
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        // Titulo
        JLabel lblTitulo = new JLabel("NUEVO USUARIO");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 18));
        lblTitulo.setForeground(TXT_MAIN);
        lblTitulo.setBounds(10, 11, 364, 30);
        contentPanel.add(lblTitulo);

        // Campos
        int y = 50;

        JLabel lblNombre = new JLabel("Nombre Completo:");
        lblNombre.setBounds(30, y, 150, 20);
        contentPanel.add(lblNombre);
        txtNombre = new JTextField();
        txtNombre.setBounds(30, y + 20, 320, 25);
        contentPanel.add(txtNombre);
        y += 50;

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setBounds(30, y, 150, 20);
        contentPanel.add(lblUsuario);
        txtUsuario = new JTextField();
        txtUsuario.setBounds(30, y + 20, 320, 25);
        contentPanel.add(txtUsuario);
        y += 50;

        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setBounds(30, y, 150, 20);
        contentPanel.add(lblPass);
        txtPassword = new JPasswordField();
        txtPassword.setBounds(30, y + 20, 320, 25);
        contentPanel.add(txtPassword);
        y += 50;

        JLabel lblConfirm = new JLabel("Confirmar Contraseña:");
        lblConfirm.setBounds(30, y, 150, 20);
        contentPanel.add(lblConfirm);
        txtConfirmPass = new JPasswordField();
        txtConfirmPass.setBounds(30, y + 20, 320, 25);
        contentPanel.add(txtConfirmPass);
        y += 50;

        JLabel lblRol = new JLabel("Rol:");
        lblRol.setBounds(30, y, 150, 20);
        contentPanel.add(lblRol);
        cmbRol = new JComboBox<>(new String[] { "empleado", "admin" });
        cmbRol.setBounds(30, y + 20, 320, 25);
        contentPanel.add(cmbRol);

        // Botones
        JPanel buttonPane = new JPanel();
        buttonPane.setBackground(BG_GOLD_DARK);
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton okButton = new JButton("GUARDAR");
        okButton.setBackground(BTN_DEFAULT);
        okButton.addActionListener(e -> registrarUsuario());
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);

        JButton cancelButton = new JButton("CANCELAR");
        cancelButton.setBackground(BTN_DEFAULT);
        cancelButton.addActionListener(e -> dispose());
        buttonPane.add(cancelButton);
    }

    private void registrarUsuario() {
        String nombre = txtNombre.getText();
        String usuario = txtUsuario.getText();
        String pass = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirmPass.getPassword());
        String rol = (String) cmbRol.getSelectedItem();

        if (nombre.isEmpty() || usuario.isEmpty() || pass.isEmpty()) {
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
        nuevo.setNombre(nombre);
        nuevo.setUsuario(usuario);
        nuevo.setPassword(pass); // En producción usaríamos hash
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
