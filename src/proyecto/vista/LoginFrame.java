package proyecto.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import proyecto.dao.UsuarioSistemaDAO;
import proyecto.modelo.UsuarioSistema;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class LoginFrame extends JFrame {

    private JPanel contentPane;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private UsuarioSistemaDAO usuarioDAO;
    private Consumer<LoadingFrame> onSuccess;

    // Colores del tema (Godhome Style)
    private final Color BG_MARBLE = new Color(233, 227, 200);
    private final Color BG_GOLD_DARK = new Color(140, 112, 60);
    private final Color BTN_DEFAULT = new Color(199, 179, 106);
    private final Color BTN_HOVER = new Color(228, 204, 130);
    private final Color TXT_MAIN = new Color(60, 45, 20);

    private JProgressBar progressBar;
    private JButton btnEntrar;
    private JButton btnRegistrar;

    public LoginFrame(Consumer<LoadingFrame> onSuccess) {
        this.onSuccess = onSuccess;
        this.usuarioDAO = new UsuarioSistemaDAO();

        setTitle("Login - Barbería Chupirules");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 450); // Increased height for progress bar
        setLocationRelativeTo(null);
        setResizable(false);
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage("src/img/logo.jpg"));
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }

        contentPane = new JPanel();
        contentPane.setBackground(proyecto.util.Theme.COLOR_PRIMARY); // Navy Background
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // Header
        JPanel panelHeader = new JPanel();
        panelHeader.setBounds(0, 0, 434, 60);
        panelHeader.setBackground(proyecto.util.Theme.COLOR_SECONDARY);
        panelHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, proyecto.util.Theme.COLOR_ACCENT_GOLD));
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
        lblUsuario.setBounds(50, 100, 100, 20);
        contentPane.add(lblUsuario);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(50, 125, 330, 30);
        txtUsuario.setFont(proyecto.util.Theme.FONT_REGULAR);
        contentPane.add(txtUsuario);
        txtUsuario.setColumns(10);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(proyecto.util.Theme.FONT_BOLD);
        lblPassword.setForeground(proyecto.util.Theme.COLOR_TEXT);
        lblPassword.setBounds(50, 170, 100, 20);
        contentPane.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(50, 195, 330, 30);
        txtPassword.setFont(proyecto.util.Theme.FONT_REGULAR);
        contentPane.add(txtPassword);

        // Botón Entrar
        btnEntrar = proyecto.util.Theme.createStyledButton("ENTRAR");
        btnEntrar.setBounds(50, 260, 150, 40);
        btnEntrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                autenticar();
            }
        });
        contentPane.add(btnEntrar);

        // Botón Registrarse
        btnRegistrar = proyecto.util.Theme.createStyledButton("REGISTRARSE");
        btnRegistrar.setBounds(230, 260, 150, 40);
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirRegistro();
            }
        });
        contentPane.add(btnRegistrar);
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
