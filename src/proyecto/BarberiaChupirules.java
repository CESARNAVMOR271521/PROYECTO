package proyecto;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class BarberiaChupirules {

    private JFrame frame;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel sidebar;

    // 🎨 ESTILO “SALÓN DE LOS DIOSES” – HOLLOW KNIGHT
    private final Color BG_MARBLE = new Color(233, 227, 200);       // Mármol claro
    private final Color BG_GOLD_SOFT = new Color(209, 184, 108);    // Dorado suave
    private final Color BG_GOLD_DARK = new Color(140, 112, 60);     // Dorado oscuro
    private final Color BG_PANEL = new Color(245, 240, 220);        // Panel suave

    private final Color BTN_DEFAULT = new Color(199, 179, 106);     // Dorado neutro
    private final Color BTN_HOVER = new Color(228, 204, 130);       // Dorado claro
    private final Color TXT_MAIN = new Color(60, 45, 20);           // Café oscuro elegante

    private final Color BORDER_GOD = new Color(242, 213, 107);      // Oro brillante

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        EventQueue.invokeLater(() -> {
            BarberiaChupirules window = new BarberiaChupirules();
            window.frame.setVisible(true);
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
        lblTitle.setFont(new Font("Serif", Font.BOLD, 28));  // tipografía elegante

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

        // MÓDULOS
        addModule(menuContainer, "Clientes", "CLIENTES", createPanel("Gestión de Clientes"));
        addModule(menuContainer, "Barberos", "BARBEROS", createPanel("Gestión de Barberos"));
        addModule(menuContainer, "Servicios", "SERVICIOS", createPanel("Catálogo de Servicios"));
        addModule(menuContainer, "Citas", "CITAS", createPanel("Agenda y Citas"));
        addModule(menuContainer, "Ventas", "VENTAS", createPanel("Punto de Venta"));
        addModule(menuContainer, "Historial", "DETALLE", createPanel("Historial de Ventas"));
        addModule(menuContainer, "Productos", "PRODUCTOS", createPanel("Gestión de Productos"));
        addModule(menuContainer, "Inventario", "INVENTARIO", createPanel("Control de Inventario"));
        addModule(menuContainer, "Usuarios", "USUARIOS", createPanel("Administración de Usuarios"));

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
        cardLayout.show(contentPanel, "CITAS");
    }

    private void addModule(JPanel container, String text, String cardName, JPanel panel) {
        JButton btn = createGodButton(text);
        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        container.add(btn);
        contentPanel.add(panel, cardName);
    }

    // ✨ BOTÓN DE ESTILO DIVINO
    private JButton createGodButton(String text) {

        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setForeground(TXT_MAIN); 
        btn.setBackground(BTN_DEFAULT);
        btn.setFont(new Font("Serif", Font.BOLD, 16));
        btn.setFocusPainted(false);

        btn.setBorder(BorderFactory.createMatteBorder(
                3, 3, 3, 3,
                BORDER_GOD
        ));

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(BTN_HOVER);
                btn.setForeground(TXT_MAIN);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(BTN_DEFAULT);
                btn.setForeground(TXT_MAIN);
            }
        });

        return btn;
    }

    // ✨ PANEL DEL CONTENIDO CENTRAL
    private JPanel createPanel(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_PANEL);

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Serif", Font.BOLD, 30));
        lbl.setForeground(TXT_MAIN);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);

        p.add(lbl, BorderLayout.CENTER);
        return p;
    }
}
