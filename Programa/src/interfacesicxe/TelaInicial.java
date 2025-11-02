package interfacesicxe;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

public class TelaInicial extends JDialog {

    public TelaInicial(JFrame parent) {
        super(parent, "Bem-vindo ao Simulador SIC/XE", true);
        FlatLightLaf.setup();
        configurarTela();
        criarComponentes();
    }

    private void configurarTela() {
        setSize(600, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(227, 242, 253));
    }

    private void criarComponentes() {
        setLayout(new BorderLayout(20, 20));

        // Painel superior com título
        JLabel lblTitulo = new JLabel("Simulador SIC/XE", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(40, 60, 120));
        add(lblTitulo, BorderLayout.NORTH);

        // Painel central com informações dos autores
        JPanel painelAutores = new JPanel(new GridLayout(0, 1, 10, 10));
        painelAutores.setBackground(new Color(240, 245, 255));
        painelAutores.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        String[] autores = {
            "Memória - Gerson",
            "Registrador - Henrique",
            "Registradores - Gustavo",
            "Executor - Carlos",
            "Instruções - Dienifer",
            "Interface - Maria Eduarda"
        };

        for (String autor : autores) {
            JLabel lblAutor = new JLabel(autor);
            lblAutor.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            lblAutor.setForeground(new Color(33, 33, 33));
            painelAutores.add(lblAutor);
        }

        add(painelAutores, BorderLayout.CENTER);

        // Painel inferior com botão
        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnIniciar = new JButton("▶ Iniciar Simulador");
        btnIniciar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnIniciar.setPreferredSize(new Dimension(200, 40));
        btnIniciar.setBackground(new Color(6,8,11));
        btnIniciar.setForeground(Color.WHITE);
        btnIniciar.setFocusPainted(false);
        btnIniciar.addActionListener(e -> {
            dispose(); // Fecha a tela inicial
            new InterfaceSICXE().setVisible(true); // Abre a interface principal
        });

        painelBotao.add(btnIniciar);
        add(painelBotao, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            new TelaInicial(frame).setVisible(true);
        });
    }
}