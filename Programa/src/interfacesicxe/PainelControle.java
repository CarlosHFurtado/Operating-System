package interfacesicxe;

import Executor.Executor;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.TitledBorder;

public class PainelControle extends JPanel {

    private static final Color COR_FUNDO_PAINEL = new Color(63, 84, 114);
    private static final Color COR_DETALHE = new Color(6,8,11);
    private static final Color COR_TEXTO = Color.WHITE;
    private static final Color COR_BOTAO = new Color(63, 84, 114);
    private static final Color COR_BOTAO_HOVER = new Color(50, 70, 95);

    private Executor executor;
    private InterfaceSICXE framePai;

    public PainelControle(Executor executor, InterfaceSICXE framePai) {
        this.executor = executor;
        this.framePai = framePai;
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COR_DETALHE, 1),
            "Controles",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            COR_DETALHE
        ));
        setBackground(COR_FUNDO_PAINEL);

        JButton btnPasso = new JButton("▶ Passo");
        JButton btnExecutar = new JButton("⏩ Executar");
        JButton btnParar = new JButton("⏹ Parar");

        estilizarBotao(btnPasso);
        estilizarBotao(btnExecutar);
        estilizarBotao(btnParar);

        btnPasso.addActionListener(e -> executarPasso());
        btnExecutar.addActionListener(e -> executarPrograma());
        btnParar.addActionListener(e -> parar());

        add(btnPasso);
        add(btnExecutar);
        add(btnParar);
    }

    private void executarPasso() {
        boolean continua = executor.executarPasso();
        if (!continua) {
            JOptionPane.showMessageDialog(this, "Fim do programa.");
        }
        framePai.atualizarTodosPaineis();
    }

    private void executarPrograma() {
        new Thread(() -> {
            executor.executarPrograma();
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Execução concluída.");
                framePai.atualizarTodosPaineis();
            });
        }).start();
    }

    private void parar() {
        // Seu Executor já lida com STOP em READ
        framePai.atualizarTodosPaineis();
    }

    private void estilizarBotao(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(COR_TEXTO);
        btn.setBackground(COR_BOTAO);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(COR_BOTAO_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(COR_BOTAO);
            }
        });
    }
}