package proyecto.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import proyecto.dao.UsuarioSistemaDAO;
import proyecto.modelo.UsuarioSistema;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class LoginFrame extends JFrame {

    private JPanel contentPane;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private UsuarioSistemaDAO usuarioDAO;
    private Consumer<LoadingFrame> onSuccess;

    private JButton btnEntrar;
    private JButton btnRegistrar;

    public LoginFrame(Consumer<LoadingFrame> onSuccess) {
        this.onSuccess = onSuccess;
        this.usuarioDAO = new UsuarioSistemaDAO();

        setTitle("Login - Barbería Chupirules");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 480, 500); // Slightly larger
        setLocationRelativeTo(null);
        setResizable(false);
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage("src/img/logo.jpg"));
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }

        contentPane = new JPanel();
        contentPane.setBackground(proyecto.util.Theme.COLOR_PRIMARY); 
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // Header
        JPanel panelHeader = new JPanel();
        panelHeader.setBounds(0, 0, 480, 70);
        panelHeader.setBackground(proyecto.util.Theme.COLOR_SECONDARY);
        // Header border
        panelHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, proyecto.util.Theme.COLOR_ACCENT_GOLD));
        contentPane.add(panelHeader);
        panelHeader.setLayout(new BorderLayout(0, 0));

        JLabel lblTitulo = new JLabel("INICIAR SESIÓN");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setFont(proyecto.util.Theme.FONT_TITLE);
        lblTitulo.setForeground(proyecto.util.Theme.COLOR_ACCENT_GOLD);
        panelHeader.add(lblTitulo);

        // Formulario
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(proyecto.util.Theme.FONT_BOLD);
        lblUsuario.setForeground(proyecto.util.Theme.COLOR_TEXT);
        lblUsuario.setBounds(60, 110, 100, 20);
        contentPane.add(lblUsuario);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(60, 135, 340, 35); // Taller input
        txtUsuario.setFont(proyecto.util.Theme.FONT_REGULAR);
        contentPane.add(txtUsuario);
        txtUsuario.setColumns(10);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(proyecto.util.Theme.FONT_BOLD);
        lblPassword.setForeground(proyecto.util.Theme.COLOR_TEXT);
        lblPassword.setBounds(60, 190, 100, 20);
        contentPane.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(60, 215, 340, 35);
        txtPassword.setFont(proyecto.util.Theme.FONT_REGULAR);
        contentPane.add(txtPassword);

        // Botón Entrar
        btnEntrar = proyecto.util.Theme.createStyledButton("ENTRAR");
        btnEntrar.setBounds(60, 290, 340, 45); // Full width button looks better
        btnEntrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                autenticar();
            }
        });
        contentPane.add(btnEntrar);

        // Botón Registrarse (Secondary style)
        btnRegistrar = proyecto.util.Theme.createSecondaryButton("REGISTRARSE");
        btnRegistrar.setBounds(60, 350, 340, 45); 
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirRegistro();
            }
        });
        contentPane.add(btnRegistrar);
        
        // Final polish: apply theme recursive to ensure text fields get rounded borders
        proyecto.util.Theme.applyRecursive(contentPane);
    }

    private void autenticar() {
        String usuario = txtUsuario.getText();
        String password = new String(txtPassword.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese usuario y contraseña", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UsuarioSistema user = usuarioDAO.login(usuario, password);
        if (user != null) {
            // Login exitoso
            dispose(); // Calla el login frame
            // Abre el loading frame
            LoadingFrame loading = new LoadingFrame(onSuccess);
            loading.startLoading();
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirRegistro() {
        new RegistroDialog(this).setVisible(true);
    }
}
