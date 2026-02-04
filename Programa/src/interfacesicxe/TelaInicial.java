package interfacesicxe;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URI;

public class TelaInicial extends JDialog {

    private static final Color BG = new Color(92, 107, 115);
    private static final Color FG = new Color(245, 245, 245);
    private static final Color LINE = new Color(220, 220, 220);

    private static final String GITHUB_URL =
            "https://github.com/CarlosHFurtado/Operating-System/tree/main";

    public TelaInicial(JFrame parent) {
        super(parent, "Bem-vindo ao Simulador SIC/XE", true);
        FlatLightLaf.setup();
        configurarTela();
        criarComponentes();
    }

    private void configurarTela() {
        setSize(700, 450);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setUndecorated(true); 
        getContentPane().setBackground(BG);
    }

    private void criarComponentes() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(20, 30, 20, 30));
        setContentPane(root);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(BG);

        JButton btnClose = new JButton("X");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btnClose.setForeground(FG); // branco
        btnClose.setBackground(BG);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());

        top.add(btnClose, BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setBackground(BG);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        root.add(center, BorderLayout.CENTER);

        JLabel lblBemVindos = new JLabel("Bem-Vindos ao");
        lblBemVindos.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblBemVindos.setForeground(FG);
        lblBemVindos.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel lblTitulo = new JLabel("Simulador SIC/XE");
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setForeground(FG);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 42));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        sep.setForeground(LINE);
        sep.setBackground(LINE);

        JButton btnColaboradores = createMenuButton("COLABORADORES");
        btnColaboradores.addActionListener(e -> new TelaColaboradores(this).setVisible(true));

        JButton btnGitHub = createMenuButton("GITHUB");
        btnGitHub.addActionListener(e -> abrirNoNavegador(GITHUB_URL));

        JButton btnIniciar = createMenuButton("INICIAR SIMULADOR");
        btnIniciar.addActionListener(e -> {
            dispose();
            new InterfaceSICXE().setVisible(true);
        });

        center.add(Box.createVerticalStrut(30));
        center.add(lblBemVindos);
        center.add(Box.createVerticalStrut(6));
        center.add(lblTitulo);
        center.add(Box.createVerticalStrut(25));
        center.add(sep);
        center.add(Box.createVerticalStrut(35));

        center.add(btnColaboradores);
        center.add(Box.createVerticalStrut(16));
        center.add(btnGitHub);
        center.add(Box.createVerticalStrut(16));
        center.add(btnIniciar);

        center.add(Box.createVerticalGlue());
    }

    private JButton createMenuButton(String text) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setPreferredSize(new Dimension(260, 42));
        b.setMaximumSize(new Dimension(260, 42));
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13)); 
        b.setBackground(new Color(245, 245, 245));
        b.setForeground(BG); 
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void abrirNoNavegador(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível abrir o navegador.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir o link:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            new TelaInicial(frame).setVisible(true);
        });
    }
}
