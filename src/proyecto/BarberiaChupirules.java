package proyecto;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class BarberiaChupirules {

    private JFrame frame;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel sidebar;

    // 🎨 ESTILO “SALÓN DE LOS DIOSES” – HOLLOW KNIGHT
    private final Color BG_MARBLE = new Color(233, 227, 200); // Mármol claro
    private final Color BG_GOLD_SOFT = new Color(209, 184, 108); // Dorado suave
    private final Color BG_GOLD_DARK = new Color(140, 112, 60); // Dorado oscuro
    private final Color BG_PANEL = new Color(245, 240, 220); // Panel suave

    private final Color BTN_DEFAULT = new Color(199, 179, 106); // Dorado neutro
    private final Color BTN_HOVER = new Color(228, 204, 130); // Dorado claro
    private final Color BTN_ACTIVE = new Color(255, 230, 150); // Dorado muy claro (Activo)
    private final Color TXT_MAIN = new Color(60, 45, 20); // Café oscuro elegante

    private final Color BORDER_GOD = new Color(242, 213, 107); // Oro brillante

    // Mapa para gestionar los botones activos
    private Map<String, JButton> menuButtons = new HashMap<>();

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        // Initialize Database
        DatabaseHelper.initDB();

        EventQueue.invokeLater(() -> {
            // Callback que inicia la aplicación principal
            Runnable startApp = () -> {
                BarberiaChupirules window = new BarberiaChupirules();
                window.frame.setVisible(true);
            };

            // Iniciar con Login
            // LoginFrame se encargará de mostrar el Splash y luego ejecutar startApp
            proyecto.vista.LoginFrame login = new proyecto.vista.LoginFrame(startApp);
            login.setVisible(true);
        });
    }

    public BarberiaChupirules() {
        initialize();
    }

    private void initialize() {

        frame = new JFrame("BARBERÍA CHUPIRULES - Godhome Edition");
        frame.setBounds(100, 100, 1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().setBackground(BG_MARBLE);

        // 🎛 SIDEBAR estilo salón divino
        sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(BG_GOLD_DARK);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 6, BORDER_GOD));

        frame.add(sidebar, BorderLayout.WEST);

        // HEADER DORADO
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_GOLD_DARK);
        header.setPreferredSize(new Dimension(250, 100));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, BORDER_GOD));

        JLabel lblTitle = new JLabel("CHUPIRULES");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(BORDER_GOD);
        lblTitle.setFont(new Font("Serif", Font.BOLD, 28)); // tipografía elegante

        JLabel lblSubtitle = new JLabel("BARBER SHOP");
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitle.setForeground(TXT_MAIN);
        lblSubtitle.setFont(new Font("Serif", Font.PLAIN, 14));

        header.add(lblTitle, BorderLayout.CENTER);
        header.add(lblSubtitle, BorderLayout.SOUTH);
        sidebar.add(header, BorderLayout.NORTH);

        // MENÚ LATERAL
        JPanel menuContainer = new JPanel(new GridLayout(0, 1, 0, 8));
        menuContainer.setBackground(BG_GOLD_DARK);
        menuContainer.setBorder(BorderFactory.createEmptyBorder(25, 10, 25, 10));
        sidebar.add(menuContainer, BorderLayout.CENTER);

        // PANEL CENTRAL
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.setBackground(BG_PANEL);
        frame.add(contentPanel, BorderLayout.CENTER);

        // MÓDULOS (F1 -> F12)
        addModule(menuContainer, "Clientes (F1)", "CLIENTES", new ClientesPanel(), "F1");
        addModule(menuContainer, "Barberos (F2)", "BARBEROS", new BarberosPanel(), "F2");
        addModule(menuContainer, "Servicios (F3)", "SERVICIOS", new ServiciosPanel(), "F3");
        addModule(menuContainer, "Citas (F4)", "CITAS", new CitasPanel(), "F4");
        addModule(menuContainer, "Ventas (F5)", "VENTAS", new VentasPanel(), "F5");
        addModule(menuContainer, "Historial (F6)", "DETALLE", new HistorialPanel(), "F6");
        addModule(menuContainer, "Productos (F7)", "PRODUCTOS", new ProductosPanel(), "F7");

        addModule(menuContainer, "Usuarios (F9)", "USUARIOS", new UsuariosPanel(), "F9");
        addModule(menuContainer, "Pagos (F10)", "PAGOS", new PagosPanel(), "F10");

        addModule(menuContainer, "Facturas (F12)", "FACTURAS", new FacturasPanel(), "F12");
        // Extra modules without shortcuts for now, or could use Shift+F1 etc.
        addModule(menuContainer, "Proveedores", "PROVEEDORES", new ProveedoresPanel(), null);
        addModule(menuContainer, "Compras", "COMPRAS", new ComprasPanel(), null);
        addModule(menuContainer, "Voz (Logs)", "VOZ_LOGS", new RegistroVozPanel(), null);

        // FOOTER SALIR
        JPanel footer = new JPanel();
        footer.setBackground(BG_GOLD_DARK);

        JButton btnSalir = createGodButton("SALIR");
        btnSalir.setBackground(new Color(150, 40, 40));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.addActionListener(e -> System.exit(0));

        footer.add(btnSalir);
        sidebar.add(footer, BorderLayout.SOUTH);

        // MÓDULO INICIAL
        setModuleActive("CITAS");
    }

    private void addModule(JPanel container, String text, String cardName, JPanel panel, String keyStroke) {
        JButton btn = createGodButton(text);
        btn.addActionListener(e -> setModuleActive(cardName));
        btn.setToolTipText("Abrir módulo de " + text.replace(" (", "").replace(")", ""));

        container.add(btn);
        contentPanel.add(panel, cardName);
        menuButtons.put(cardName, btn);

        // ⌨️ BINDING DE TECLADO
        if (keyStroke != null) {
            InputMap inputMap = contentPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap actionMap = contentPanel.getActionMap();

            inputMap.put(KeyStroke.getKeyStroke(keyStroke), cardName);
            actionMap.put(cardName, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setModuleActive(cardName);
                }
            });
        }
    }

    private void setModuleActive(String cardName) {
        cardLayout.show(contentPanel, cardName);

        // Actualizar visualmente los botones
        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            if (entry.getKey().equals(cardName)) {
                entry.getValue().setBackground(BTN_ACTIVE);
                entry.getValue().setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.WHITE)); // Resalte extra
            } else {
                entry.getValue().setBackground(BTN_DEFAULT);
                entry.getValue().setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, BORDER_GOD));
            }
        }
    }

    // ✨ BOTÓN DE ESTILO DIVINO
    private JButton createGodButton(String text) {

        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(200, 35)); // Ligeramente más pequeño para que quepan todos
        btn.setForeground(TXT_MAIN);
        btn.setBackground(BTN_DEFAULT);
        btn.setFont(new Font("Serif", Font.BOLD, 14)); // Fuente ajustada
        btn.setFocusPainted(false);

        btn.setBorder(BorderFactory.createMatteBorder(
                3, 3, 3, 3,
                BORDER_GOD));

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Solo cambiar si no es el botón activo
                if (btn.getBackground() != BTN_ACTIVE) {
                    btn.setBackground(BTN_HOVER);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                // Solo restaurar si no es el botón activo
                if (btn.getBackground() != BTN_ACTIVE) {
                    btn.setBackground(BTN_DEFAULT);
                }
            }
        });

        return btn;
    }

    // ✨ PANEL DEL CONTENIDO CENTRAL

}
