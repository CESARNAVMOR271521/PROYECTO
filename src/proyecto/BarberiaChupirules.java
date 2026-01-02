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

    private JLabel lblTitle;
    private JLabel lblSubtitle;

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

        lblTitle = new JLabel("CHUPIRULES");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(Theme.COLOR_ACCENT_GOLD);
        lblTitle.setFont(Theme.FONT_TITLE);

        lblSubtitle = new JLabel("BARBER SHOP");
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitle.setForeground(Theme.COLOR_TEXT_MUTED);
        lblSubtitle.setFont(Theme.FONT_SUBTITLE);

        // Load custom name if exists
        loadConfig();

        header.add(lblTitle, BorderLayout.CENTER);
        header.add(lblSubtitle, BorderLayout.SOUTH);
        sidebar.add(header, BorderLayout.NORTH);

        // MENÚ LATERAL
        JPanel menuContainer = new JPanel(); 
        menuContainer.setLayout(new javax.swing.BoxLayout(menuContainer, javax.swing.BoxLayout.Y_AXIS));
        menuContainer.setBackground(Theme.COLOR_SECONDARY);
        menuContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidebar.add(menuContainer, BorderLayout.CENTER);

        // PANEL CENTRAL
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.setBackground(Theme.COLOR_PRIMARY); 
        frame.add(contentPanel, BorderLayout.CENTER);

        if (loader != null) loader.updateProgress(25, "Cargando componentes...");

        // === SECCIÓN: PRINCIPAL ===
        // addMenuSection(menuContainer, "PRINCIPAL");
        
        // 🏠 DASHBOARD (Inicio) -> F1
        addModule(menuContainer, "🏠 Inicio (F1)", "INICIO", new DashboardPanel(this::setModuleActive), "F1");

        // 💰 Ventas & 📅 Citas (Core)
        addModule(menuContainer, "💰 Ventas (F2)", "VENTAS", new VentasPanel(), "F2");
        addModule(menuContainer, "📅 Citas (F3)", "CITAS", new CitasPanel(), "F3");

        addMenuSpacer(menuContainer);
        addMenuSection(menuContainer, "GESTIÓN");
        
        // 👥 Clientes
        addModule(menuContainer, "👥 Clientes (F4)", "CLIENTES", new ClientesPanel(), "F4");
        // 📦 Productos & 💇 Servicios
        addModule(menuContainer, "📦 Productos (F5)", "PRODUCTOS", new ProductosPanel(), "F5");
        addModule(menuContainer, "💇 Servicios (F6)", "SERVICIOS", new ServiciosPanel(), "F6");
        // 💈 Barberos
        addModule(menuContainer, "💈 Barberos (F7)", "BARBEROS", new BarberosPanel(), "F7");

        addMenuSpacer(menuContainer);
        addMenuSection(menuContainer, "ADMINISTRACIÓN");

        // 🛒 Compras & 🏭 Proveedores
        addModule(menuContainer, "🛒 Compras (F8)", "COMPRAS", new ComprasPanel(), "F8");
        addModule(menuContainer, "🏭 Proveedores (F9)", "PROVEEDORES", new ProveedoresPanel(), "F9");
        // 📜 Historial & 🧾 Facturas
        addModule(menuContainer, "📜 Historial (F10)", "DETALLE", new HistorialPanel(), "F10");
        addModule(menuContainer, "🧾 Facturas (F11)", "FACTURAS", new FacturasPanel(), "F11");

        addMenuSpacer(menuContainer);
        addMenuSection(menuContainer, "SISTEMA");
        
        // 👥 Usuarios & 💳 Pagos & 🎤 Voz
        addModule(menuContainer, "⚙️ Usuarios (Shft+F1)", "USUARIOS", new UsuariosPanel(), "shift F1");
        addModule(menuContainer, "💳 Pagos (Shft+F2)", "PAGOS", new PagosPanel(), "shift F2");
        addModule(menuContainer, "🎤 Voz (Shft+F3)", "VOZ_LOGS", new RegistroVozPanel(), "shift F3");
        
        // 🔧 Configuración (New Panel)
        addModule(menuContainer, "🔧 Configuración (Shft+F4)", "CONFIG", new ConfiguracionPanel(), "shift F4");
        
        // FOOTER SALIR
        JPanel footer = new JPanel();
        footer.setBackground(Theme.COLOR_SECONDARY);
        footer.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JButton btnSalir = createMenuButton("🚪 SALIR");
        btnSalir.setBackground(Theme.COLOR_ACCENT_RED);
        btnSalir.setForeground(Color.WHITE);
        // btnSalir.setBorder(new Theme.RoundedBorder(10, Theme.COLOR_ACCENT_RED)); // Replaced by custom paint
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
    
    // --- PERSISTENCIA DE CONFIGURACIÓN ---
    
    // Loaded via proyecto.util.Config
    
    private void loadConfig() {
        String nombre = proyecto.util.Config.get("nombre_negocio", "CHUPIRULES");
        if (lblTitle != null) lblTitle.setText(nombre.toUpperCase());
    }
    
    private void cambiarNombreNegocio() {
        new proyecto.vista.ConfiguracionDialog(frame, this::loadConfig).setVisible(true);
    }

    private void addModule(JPanel container, String text, String cardName, JPanel panel, String keyStroke) {
        JButton btn = createMenuButton(text);
        btn.addActionListener(e -> setModuleActive(cardName));
        btn.setToolTipText("Abrir módulo de " + text.replace(" (", "").replace(")", ""));

        container.add(btn);
        // Add spacing between buttons
        container.add(javax.swing.Box.createVerticalStrut(8));
        
        // Force apply recursive theme
        Theme.applyRecursive(panel);
        
        contentPanel.add(panel, cardName);
        menuButtons.put(cardName, btn);
        loadedPanels.put(cardName, panel);

        // ⌨️ BINDING DE TECLADO
        if (keyStroke != null) {
            InputMap inputMap = contentPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap actionMap = contentPanel.getActionMap();
            
            KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
            if (ks != null) inputMap.put(ks, cardName); // Check null
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
        
        // Check for dynamic title update from Config
        if (cardName.equals("INICIO") || cardName.equals("CONFIG")) {
            loadConfig(); 
        }

        // Actualizar visualmente los botones
        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            JButton b = entry.getValue();
            if (entry.getKey().equals(cardName)) {
                // Activo
                b.setBackground(Theme.COLOR_PRIMARY); // Merge with content
                b.setForeground(Theme.COLOR_ACCENT_GOLD);
                b.setFont(Theme.FONT_BOLD);
                b.repaint(); // Force paint
            } else {
                // Inactivo
                b.setBackground(Color.BLACK); // Black background
                b.setForeground(Color.WHITE); // High visibility
                b.setFont(Theme.FONT_BOLD);
                b.repaint();
            }
        }
    }

    private void addMenuSection(JPanel container, String title) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10)); 
        lbl.setForeground(new Color(150, 150, 150)); // Muted header
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 0));
        lbl.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        container.add(lbl);
    }

    private void addMenuSpacer(JPanel container) {
        container.add(javax.swing.Box.createRigidArea(new Dimension(0, 15))); // Increased section spacing
    }

    // ✨ BOTÓN DE MENÚ LATERAL - MODERNO
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Paint custom rounded background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                
                // Paint White Border
                g2.setColor(Color.WHITE);
                g2.setStroke(new java.awt.BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                
                // Active Indicator Line (if active/gold)
                if (getForeground().equals(Theme.COLOR_ACCENT_GOLD)) {
                    g2.setColor(Theme.COLOR_ACCENT_GOLD);
                    // Slightly adjust indicator to fit inside border
                    g2.fillRect(4, 8, 4, getHeight()-16);
                }
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        btn.setMaximumSize(new Dimension(Short.MAX_VALUE, 45)); 
        btn.setPreferredSize(new Dimension(220, 45)); 
        btn.setForeground(Color.WHITE); 
        btn.setBackground(Color.BLACK); // Default Black
        btn.setFont(Theme.FONT_BOLD); 
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(12);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(JComponent.LEFT_ALIGNMENT); 
        
        // Critical for custom painting transparency
        btn.setContentAreaFilled(false); 
        btn.setBorderPainted(false);
        // Add padding via border but don't paint it
        btn.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0)); // Slightly more padding to clear border

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!btn.getForeground().equals(Theme.COLOR_ACCENT_GOLD)) { 
                     btn.setBackground(new Color(40, 40, 40)); // Dark Gray
                     btn.repaint();
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!btn.getForeground().equals(Theme.COLOR_ACCENT_GOLD)) {
                    btn.setBackground(Color.BLACK); // Revert
                    btn.repaint();
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
