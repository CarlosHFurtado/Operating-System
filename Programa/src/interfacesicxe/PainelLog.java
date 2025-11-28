package interfacesicxe;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.TitledBorder;

public class PainelLog extends JPanel {

    private static final Color COR_FUNDO_PAINEL = new Color(63, 84, 114); 
    private static final Color COR_DETALHE = new Color(6, 8, 11);       
    private static final Color COR_TEXTO = Color.WHITE;

    // Instância única para acesso global
    private static PainelLog instance;

    private JTextArea areaLog;

    public PainelLog() {
        // Salva a instância para uso global
        instance = this;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COR_DETALHE, 1),
            "Log de Execução",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            COR_DETALHE
        ));
        setBackground(COR_FUNDO_PAINEL);

        areaLog = new JTextArea();
        areaLog.setFont(new Font("Consolas", Font.PLAIN, 13));
        areaLog.setForeground(COR_TEXTO);
        areaLog.setEditable(false);
        areaLog.setBackground(COR_FUNDO_PAINEL);
        areaLog.setLineWrap(true);
        areaLog.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(areaLog);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(COR_FUNDO_PAINEL);
        add(scroll, BorderLayout.CENTER);

        adicionarMensagem("Simulador iniciado.");
        adicionarMensagem("Aguardando carregamento do programa...");
    }

    // Método para adicionar mensagens na interface
    public void adicionarMensagem(String msg) {
        areaLog.append("  " + msg + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }

    // Método estático para logar de qualquer classe
    public static void logGlobal(String msg) {
        if (instance != null) {
            instance.adicionarMensagem(msg);
        }
    }

    public void limpar() {
        areaLog.setText("");
        adicionarMensagem("Log limpo.");
    }
}