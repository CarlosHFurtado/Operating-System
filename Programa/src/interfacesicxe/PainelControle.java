package interfacesicxe;

import Executor.Executor;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class PainelControle extends JPanel {

    private static final Color COR_FUNDO_PAINEL = new Color(63, 84, 114);
    private static final Color COR_DETALHE = new Color(6, 8, 11);
    private static final Color COR_TEXTO = Color.WHITE;
    private static final Color COR_BOTAO = new Color(63, 84, 114);
    private static final Color COR_BOTAO_HOVER = new Color(50, 70, 95);

    private final Executor executor;
    private final InterfaceSICXE framePai;

    // Entrada RD (simulada)
    private JTextField campoEntradaHex;
    private JButton btnEnviarRD;

    // Se RD acontecer durante "Executar", retomamos automaticamente após enviar
    private boolean retomarAutomaticoAposRD = false;

    public PainelControle(Executor executor, InterfaceSICXE framePai) {
        this.executor = executor;
        this.framePai = framePai;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COR_DETALHE, 1),
                "Controles",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                COR_DETALHE
        ));
        setBackground(COR_FUNDO_PAINEL);

        add(criarPainelEntradaRD(), BorderLayout.NORTH);
        add(criarPainelBotoes(), BorderLayout.CENTER);
    }

    private JPanel criarPainelEntradaRD() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        p.setBackground(COR_FUNDO_PAINEL);

        JLabel lbl = new JLabel("Entrada RD (hex 00–FF):");
        lbl.setForeground(COR_TEXTO);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));

        campoEntradaHex = new JTextField(6);
        campoEntradaHex.setFont(new Font("Consolas", Font.PLAIN, 13));
        campoEntradaHex.setToolTipText("Digite 2 dígitos hex (ex: A5) ou 0xA5");

        btnEnviarRD = new JButton("Enviar");
        estilizarBotao(btnEnviarRD);

        btnEnviarRD.addActionListener(e -> enviarEntradaRD());
        campoEntradaHex.addActionListener(e -> enviarEntradaRD()); // Enter envia

        // Começa desabilitado: só habilita quando o executor parar em RD
        campoEntradaHex.setEnabled(false);
        btnEnviarRD.setEnabled(false);

        p.add(lbl);
        p.add(campoEntradaHex);
        p.add(btnEnviarRD);

        return p;
    }

    private JPanel criarPainelBotoes() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        p.setBackground(COR_FUNDO_PAINEL);

        JButton btnPasso = new JButton("▶ Passo");
        JButton btnExecutar = new JButton("⏩ Executar");
        JButton btnParar = new JButton("⏹ Parar");

        estilizarBotao(btnPasso);
        estilizarBotao(btnExecutar);
        estilizarBotao(btnParar);

        btnPasso.addActionListener(e -> executarPasso());
        btnExecutar.addActionListener(e -> executarPrograma());
        btnParar.addActionListener(e -> parar());

        p.add(btnPasso);
        p.add(btnExecutar);
        p.add(btnParar);
        return p;
    }

    private void executarPasso() {
        // Se estiver aguardando RD, não executa mais nada sem enviar entrada
        if (executor.estaAguardandoEntradaRD()) {
            habilitarEntradaRD(false);
            return;
        }

        boolean continua = executor.executarPasso();
        framePai.atualizarTodosPaineis();

        if (!continua) {
            if (executor.estaAguardandoEntradaRD()) {
                habilitarEntradaRD(false); // passo-a-passo: não retoma automático
            } else {
                JOptionPane.showMessageDialog(this, "Fim do programa.");
            }
        }
    }

    private void executarPrograma() {
        // Se estiver aguardando RD, não inicia thread nova: só habilita entrada
        if (executor.estaAguardandoEntradaRD()) {
            habilitarEntradaRD(true);
            return;
        }

        new Thread(() -> {
            executor.executarPrograma();

            SwingUtilities.invokeLater(() -> {
                framePai.atualizarTodosPaineis();

                if (executor.estaAguardandoEntradaRD()) {
                    habilitarEntradaRD(true); // executar: retoma automático após enviar
                    return;
                }

                JOptionPane.showMessageDialog(this, "Execução concluída.");
            });
        }).start();
    }

    private void parar() {
        executor.parar();
        retomarAutomaticoAposRD = false;

        campoEntradaHex.setEnabled(false);
        btnEnviarRD.setEnabled(false);

        framePai.atualizarTodosPaineis();
    }

    private void habilitarEntradaRD(boolean retomarAutomatico) {
        retomarAutomaticoAposRD = retomarAutomatico;

        campoEntradaHex.setEnabled(true);
        btnEnviarRD.setEnabled(true);
        campoEntradaHex.requestFocusInWindow();
        campoEntradaHex.selectAll();
    }

    private void enviarEntradaRD() {
        if (!executor.estaAguardandoEntradaRD()) {
            campoEntradaHex.setEnabled(false);
            btnEnviarRD.setEnabled(false);
            return;
        }

        try {
            int byteLido = parseHexByte(campoEntradaHex.getText());
            executor.fornecerEntradaRD(byteLido);

            campoEntradaHex.setEnabled(false);
            btnEnviarRD.setEnabled(false);

            framePai.atualizarTodosPaineis();

            // Se estava no modo "Executar", retoma automaticamente
            if (retomarAutomaticoAposRD) {
                retomarAutomaticoAposRD = false;
                executarPrograma();
            }

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Entrada inválida", JOptionPane.ERROR_MESSAGE);
            campoEntradaHex.requestFocusInWindow();
            campoEntradaHex.selectAll();
        }
    }

    private int parseHexByte(String s) {
        if (s == null) throw new IllegalArgumentException("Entrada vazia.");
        String t = s.trim();
        if (t.isEmpty()) throw new IllegalArgumentException("Entrada vazia.");

        if (t.startsWith("0x") || t.startsWith("0X")) {
            t = t.substring(2).trim();
        }

        if (!t.matches("[0-9a-fA-F]{1,2}")) {
            throw new IllegalArgumentException("Digite um byte em HEX (00–FF). Ex: A5 ou 0xA5.");
        }

        int valor = Integer.parseInt(t, 16);
        if (valor < 0 || valor > 255) {
            throw new IllegalArgumentException("Valor fora do intervalo 00–FF.");
        }
        return valor;
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
