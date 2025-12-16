package proyecto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import proyecto.dao.RegistroVozDAO;

public class RegistroVozPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private RegistroVozDAO dao;

    // Theme Colors
    private final Color BTN_DEFAULT = new Color(199, 179, 106);
    private final Color TXT_MAIN = new Color(60, 45, 20);

    public RegistroVozPanel() {
        dao = new RegistroVozDAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 240, 220));

        JLabel lblTitle = new JLabel("Historial de Comandos de Voz");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 24));
        lblTitle.setForeground(TXT_MAIN);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        String[] columnNames = { "ID", "Texto Reconocido", "Fecha/Hora" };
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        // Hide ID
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(new Color(245, 240, 220));

        JButton btnRefresh = new JButton("Actualizar");
        btnRefresh.setBackground(BTN_DEFAULT);
        btnRefresh.setForeground(TXT_MAIN);
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> loadData());

        btnPanel.add(btnRefresh);
        add(btnPanel, BorderLayout.SOUTH);

        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<String[]> logs = dao.listar();
        for (String[] log : logs) {
            tableModel.addRow(log);
        }
    }
}
