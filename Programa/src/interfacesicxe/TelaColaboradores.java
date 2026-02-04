package interfacesicxe;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URI;

public class TelaColaboradores extends JDialog {

    private static final Color BG = new Color(92, 107, 115);
    private static final Color FG = new Color(245, 245, 245);
    private static final Color LINE = new Color(220, 220, 220);

    private static final String[][] COLABS = {
            {"CARLOS HENRIQUE GOEBEL TEIXEIRA FURTADO", "https://github.com/CarlosHFurtado"},
            {"MARIA EDUARDO ATENCIO CÂNDIA",            "https://github.com/alpaca157"},
            {"DIENIFER BIERHALS LEDEBUHR",              "https://github.com/DLedebuhr"},
            {"GERSON FARIAS CLARA",                     "https://github.com/Gerson-Clara"},
            {"GUSTAVO DOMÊNECH DE SOUZA",               "https://github.com/GustavoDomenech"},
            {"HENRIQUE VALEZA DOMINGUES",               "https://github.com/henriquevalezad"}
    };

    public TelaColaboradores(Dialog parent) {
        super(parent, "Colaboradores", true);
        configurarTela();
        criarComponentes();
    }

    private void configurarTela() {
        setSize(700, 450);
        setLocationRelativeTo(getParent());
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
        btnClose.setForeground(FG);
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

        JSeparator topLine = new JSeparator(SwingConstants.HORIZONTAL);
        topLine.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        topLine.setPreferredSize(new Dimension(1, 2));
        topLine.setForeground(LINE);
        topLine.setOpaque(true);

        JSeparator bottomLine = new JSeparator(SwingConstants.HORIZONTAL);
        bottomLine.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        bottomLine.setPreferredSize(new Dimension(1, 2));
        bottomLine.setForeground(LINE);
        bottomLine.setOpaque(true);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);

        Font nomeFont = new Font("Segoe UI", Font.PLAIN, 14);

        for (String[] c : COLABS) {
            String nome = c[0];
            String url = c[1];
            
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(10, 40, 10, 0); // margem esquerda maior

            JLabel lbl = new JLabel(nome);
            lbl.setForeground(FG);
            lbl.setFont(nomeFont);
            grid.add(lbl, gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.weightx = 0.0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.insets = new Insets(10, 0, 10, 40); // margem direita maior

            JButton btn = createGithubButton();
            btn.addActionListener(e -> abrirNoNavegador(url));
            grid.add(btn, gbc);

            gbc.gridy++;
            gbc.insets = new Insets(10, 0, 10, 0);
        }

        center.add(Box.createVerticalStrut(25));
        center.add(topLine);
        center.add(Box.createVerticalStrut(30));

        center.add(grid);

        center.add(Box.createVerticalStrut(30));
        center.add(bottomLine);
        center.add(Box.createVerticalStrut(25));
    }

    private JButton createGithubButton() {
        JButton b = new JButton("GITHUB");
        b.setPreferredSize(new Dimension(90, 28));
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12)); 
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
}
