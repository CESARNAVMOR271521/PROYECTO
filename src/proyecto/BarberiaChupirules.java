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
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.InputMap;

import proyecto.util.Theme;
import proyecto.vista.LoginFrame;

public class BarberiaChupirules {

    private JFrame frame;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel sidebar;

    // Mapa para gestionar los botones activos
    private Map<String, JButton> menuButtons = new HashMap<>();
    // Registry for panels
    private Map<String, JPanel> loadedPanels = new HashMap<>();
    private String currentModule = "INICIO";

    public static void main(String[] args) {
        try {
            // Inicializar tema global ANTES de crear componentes
            Theme.setupUI();
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        // Initialize Database
        DatabaseHelper.initDB();

        EventQueue.invokeLater(() -> {
            // Callback que inicia la aplicación principal con el loader
            java.util.function.Consumer<proyecto.vista.LoadingFrame> startApp = (loader) -> {
                // Initialize in background/worker thread context (which LoadingFrame provides)
                // But create frame.setVisible in EDT
                try {
                    BarberiaChupirules window = new BarberiaChupirules(loader);
                    EventQueue.invokeLater(() -> window.frame.setVisible(true));
                } catch (Throwable e) {
                    e.printStackTrace();
                    javax.swing.JOptionPane.showMessageDialog(null, 
                        "Error fatal iniciando la aplicación:\n" + e.getMessage(), 
                        "Error de Inicio", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            };

            // Iniciar con Login
            LoginFrame login = new LoginFrame(startApp);
            login.setVisible(true);
        });
    }

    public BarberiaChupirules() {
        // Default constructor for design preview or legacy
        initialize(null);
    }
    
    public BarberiaChupirules(proyecto.vista.LoadingFrame loader) {
        initialize(loader);
    }

    private void initialize(proyecto.vista.LoadingFrame loader) {
        if (loader != null) loader.updateProgress(10, "Configurando ventana principal...");

        frame = new JFrame("BARBERÍA CHUPIRULES - Management System");
        frame.setBounds(100, 100, 1280, 850); 
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            frame.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("src/img/logo.jpg"));
        } catch (Exception e) {
             System.err.println("Error loading icon: " + e.getMessage());
        }
        frame.setLocationRelativeTo(null); 
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Theme.COLOR_PRIMARY);

        if (loader != null) loader.updateProgress(20, "Cargando componentes visuales...");

        // 🎛 SIDEBAR
        sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(Theme.COLOR_SECONDARY);
        // Subtle border
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0,0,0,30)));

        frame.add(sidebar, BorderLayout.WEST);

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.COLOR_SECONDARY); 
        header.setPreferredSize(new Dimension(260, 120));
        header.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        JLabel lblTitle = new JLabel("CHUPIRULES");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(Theme.COLOR_ACCENT_GOLD);
        lblTitle.setFont(Theme.FONT_TITLE);

        JLabel lblSubtitle = new JLabel("BARBER SHOP");
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitle.setForeground(Theme.COLOR_TEXT_MUTED);
        lblSubtitle.setFont(Theme.FONT_SUBTITLE);

        header.add(lblTitle, BorderLayout.CENTER);
        header.add(lblSubtitle, BorderLayout.SOUTH);
        sidebar.add(header, BorderLayout.NORTH);

        // MENÚ LATERAL
        JPanel menuContainer = new JPanel(new GridLayout(0, 1, 0, 5)); 
        menuContainer.setBackground(Theme.COLOR_SECONDARY);
        menuContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidebar.add(menuContainer, BorderLayout.CENTER);

        // PANEL CENTRAL
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.setBackground(Theme.COLOR_PRIMARY); 
        frame.add(contentPanel, BorderLayout.CENTER);

        if (loader != null) loader.updateProgress(25, "Cargando Dashboard...");
        // 🏠 DASHBOARD (Inicio)
        addModule(menuContainer, "Inicio", "INICIO", new DashboardPanel(), null);

        if (loader != null) loader.updateProgress(30, "Cargando módulo de Clientes...");
        // MÓDULOS (F1 -> F12)
        addModule(menuContainer, "Clientes (F1)", "CLIENTES", new ClientesPanel(), "F1");
        
        if (loader != null) loader.updateProgress(35, "Cargando módulo de Barberos...");
        addModule(menuContainer, "Barberos (F2)", "BARBEROS", new BarberosPanel(), "F2");
        
        if (loader != null) loader.updateProgress(40, "Cargando Servicios...");
        addModule(menuContainer, "Servicios (F3)", "SERVICIOS", new ServiciosPanel(), "F3");
        
        if (loader != null) loader.updateProgress(45, "Cargando Citas...");
        addModule(menuContainer, "Citas (F4)", "CITAS", new CitasPanel(), "F4");
        
        if (loader != null) loader.updateProgress(50, "Cargando Ventas...");
        addModule(menuContainer, "Ventas (F5)", "VENTAS", new VentasPanel(), "F5");
        
        if (loader != null) loader.updateProgress(55, "Cargando Historial...");
        addModule(menuContainer, "Historial (F6)", "DETALLE", new HistorialPanel(), "F6");
        
        if (loader != null) loader.updateProgress(60, "Cargando Productos...");
        addModule(menuContainer, "Productos (F7)", "PRODUCTOS", new ProductosPanel(), "F7");

        if (loader != null) loader.updateProgress(65, "Cargando Proveedores...");
        addModule(menuContainer, "Proveedores (F8)", "PROVEEDORES", new ProveedoresPanel(), "F8");

        if (loader != null) loader.updateProgress(70, "Cargando Usuarios y Pagos...");
        addModule(menuContainer, "Usuarios (F9)", "USUARIOS", new UsuariosPanel(), "F9");
        addModule(menuContainer, "Pagos (F10)", "PAGOS", new PagosPanel(), "F10");

        if (loader != null) loader.updateProgress(75, "Cargando Compras...");
        addModule(menuContainer, "Compras (F11)", "COMPRAS", new ComprasPanel(), "F11");

        if (loader != null) loader.updateProgress(80, "Cargando Facturación...");
        addModule(menuContainer, "Facturas (F12)", "FACTURAS", new FacturasPanel(), "F12");
        
        addModule(menuContainer, "Voz (Shift+F12)", "VOZ_LOGS", new RegistroVozPanel(), "shift F12");

        // FOOTER SALIR
        JPanel footer = new JPanel();
        footer.setBackground(Theme.COLOR_SECONDARY);
        footer.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JButton btnSalir = createMenuButton("SALIR");
        btnSalir.setBackground(Theme.COLOR_ACCENT_RED);
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setBorder(new Theme.RoundedBorder(10, Theme.COLOR_ACCENT_RED));
        // Need to override logic for custom painting on exit button or keep as menu button style
        // Let's keep consistent but red
        btnSalir.addActionListener(e -> System.exit(0));

        footer.add(btnSalir);
        sidebar.add(footer, BorderLayout.SOUTH);

        // MÓDULO INICIAL
        setModuleActive("INICIO");
        
        if (loader != null) loader.updateProgress(90, "Iniciando Asistente IA...");
        
        // 🧠 INICIAR ASISTENTE IA
        try {
            AsistenteIA asistente = new AsistenteIA(this);
            asistente.iniciarEscucha();
            System.out.println("Asistente IA iniciado y escuchando...");
        } catch (Throwable e) {
            System.err.println("Error al iniciar asistente IA: " + e.getMessage());
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(frame, 
                "No se pudo iniciar el Asistente de Voz (Librerías faltantes o error).\n" +
                "La aplicación continuará sin funciones de voz.\n\n" + e.getMessage(),
                "Advertencia de Voz", javax.swing.JOptionPane.WARNING_MESSAGE);
        }
        
        if (loader != null) loader.updateProgress(100, "¡Bienvenido!");
    }

    private void addModule(JPanel container, String text, String cardName, JPanel panel, String keyStroke) {
        JButton btn = createMenuButton(text);
        btn.addActionListener(e -> setModuleActive(cardName));
        btn.setToolTipText("Abrir módulo de " + text.replace(" (", "").replace(")", ""));

        container.add(btn);
        
        // Force apply recursive theme
        Theme.applyRecursive(panel);
        
        contentPanel.add(panel, cardName);
        menuButtons.put(cardName, btn);
        loadedPanels.put(cardName, panel);

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

    public void setModuleActive(String cardName) {
        cardLayout.show(contentPanel, cardName);
        this.currentModule = cardName;

        // Actualizar visualmente los botones
        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            if (entry.getKey().equals(cardName)) {
                // Activo
                JButton b = entry.getValue();
                b.setBackground(Theme.COLOR_PRIMARY); // Merge with content
                b.setForeground(Theme.COLOR_ACCENT_GOLD);
                b.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, Theme.COLOR_ACCENT_GOLD));
                b.setFont(Theme.FONT_BOLD);
            } else {
                // Inactivo
                JButton b = entry.getValue();
                b.setBackground(Theme.COLOR_SECONDARY);
                b.setForeground(Theme.COLOR_TEXT);
                b.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
                b.setFont(Theme.FONT_REGULAR);
            }
        }
    }

    // ✨ BOTÓN DE MENÚ LATERAL
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(220, 40));
        btn.setForeground(Theme.COLOR_TEXT);
        btn.setBackground(Theme.COLOR_SECONDARY);
        btn.setFont(Theme.FONT_REGULAR);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(15);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Hover effect
                if (!btn.getForeground().equals(Theme.COLOR_ACCENT_GOLD)) { // If not active
                     btn.setBackground(Theme.COLOR_SECONDARY.brighter());
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!btn.getForeground().equals(Theme.COLOR_ACCENT_GOLD)) {
                    btn.setBackground(Theme.COLOR_SECONDARY);
                }
            }
        });

        return btn;
    }
    public void dispatchVoiceCommand(String module, String command, String args) {
        String targetModule = (module == null || module.equals("NULL")) ? currentModule : module;
        
        // If module is specified and different, switch to it first
        if (module != null && !module.equals("NULL") && !module.equals(currentModule)) {
            setModuleActive(targetModule);
        }

        JPanel panel = loadedPanels.get(targetModule);
        if (panel == null) {
            System.err.println("Panel no encontrado para modulo: " + targetModule);
            return;
        }

        if (panel instanceof VoiceAware) {
            ((VoiceAware) panel).handleVoiceCommand(command.toUpperCase(), args);
        } else {
            System.out.println("Panel " + targetModule + " no soporta comandos de voz (VoiceAware).");
            // Optional: Feedback to user
            // proyecto.TextToSpeech.speak("Este modulo no soporta comandos de voz.");
        }
    }
}
