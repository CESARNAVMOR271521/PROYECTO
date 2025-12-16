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

public class LoginFrame extends JFrame {

    private JPanel contentPane;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private UsuarioSistemaDAO usuarioDAO;
    private Runnable onSuccess;

    // Colores del tema (Godhome Style)
    private final Color BG_MARBLE = new Color(233, 227, 200);
    private final Color BG_GOLD_DARK = new Color(140, 112, 60);
    private final Color BTN_DEFAULT = new Color(199, 179, 106);
    private final Color BTN_HOVER = new Color(228, 204, 130);
    private final Color TXT_MAIN = new Color(60, 45, 20);

    public LoginFrame(Runnable onSuccess) {
        this.onSuccess = onSuccess;
        this.usuarioDAO = new UsuarioSistemaDAO();

        setTitle("Login - Barbería Chupirules");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        contentPane = new JPanel();
        contentPane.setBackground(BG_MARBLE);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // Header
        JPanel panelHeader = new JPanel();
        panelHeader.setBounds(0, 0, 434, 60);
        panelHeader.setBackground(BG_GOLD_DARK);
        contentPane.add(panelHeader);
        panelHeader.setLayout(new BorderLayout(0, 0));

        JLabel lblTitulo = new JLabel("INICIAR SESIÓN");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        panelHeader.add(lblTitulo);

        // Formulario
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Serif", Font.BOLD, 14));
        lblUsuario.setForeground(TXT_MAIN);
        lblUsuario.setBounds(50, 100, 100, 20);
        contentPane.add(lblUsuario);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(50, 125, 330, 30);
        txtUsuario.setFont(new Font("SansSerif", Font.PLAIN, 14));
        contentPane.add(txtUsuario);
        txtUsuario.setColumns(10);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Serif", Font.BOLD, 14));
        lblPassword.setForeground(TXT_MAIN);
        lblPassword.setBounds(50, 170, 100, 20);
        contentPane.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(50, 195, 330, 30);
        txtPassword.setFont(new Font("SansSerif", Font.PLAIN, 14));
        contentPane.add(txtPassword);

        // Botón Entrar
        JButton btnEntrar = createStyledButton("ENTRAR");
        btnEntrar.setBounds(50, 260, 150, 40);
        btnEntrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                autenticar();
            }
        });
        contentPane.add(btnEntrar);

        // Botón Registrarse
        JButton btnRegistrar = createStyledButton("REGISTRARSE");
        btnRegistrar.setBounds(230, 260, 150, 40);
        btnRegistrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                abrirRegistro();
            }
        });
        contentPane.add(btnRegistrar);
    }

    private void autenticar() {
        String usuario = txtUsuario.getText();
        String password = new String(txtPassword.getPassword());

        UsuarioSistema u = usuarioDAO.login(usuario, password);

        if (u != null) {
            // Login exitoso
            this.dispose(); // Cerrar login
            // Iniciar SlashScreen y luego Main
            SplashScreen splash = new SplashScreen(onSuccess);
            splash.start();
        } else {
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirRegistro() {
        RegistroDialog reg = new RegistroDialog(this);
        reg.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(BTN_DEFAULT);
        btn.setForeground(TXT_MAIN);
        btn.setFont(new Font("Serif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(BG_GOLD_DARK, 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(BTN_HOVER);
            }

            public void mouseExited(MouseEvent evt) {
                btn.setBackground(BTN_DEFAULT);
            }
        });
        return btn;
    }
}
