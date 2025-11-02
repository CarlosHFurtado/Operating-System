package interfacesicxe;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.TitledBorder;

public class PainelLog extends JPanel {

    private static final Color COR_FUNDO_PAINEL = new Color(63, 84, 114); 
    private static final Color COR_DETALHE = new Color(6,8,11);       
    private static final Color COR_TEXTO = Color.black;                    

    private JTextArea areaLog;

    public PainelLog() {
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
        areaLog.setBackground(COR_FUNDO_PAINEL); // ← MESMO FUNDO QUE O PAINEL!
        areaLog.setLineWrap(true);
        areaLog.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(areaLog);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(COR_FUNDO_PAINEL); // ← Fundo do viewport
        add(scroll, BorderLayout.CENTER);

        adicionarMensagem("Simulador iniciado.");
        adicionarMensagem("Aguardando carregamento do programa...");
    }

    public void adicionarMensagem(String msg) {
        areaLog.append("> " + msg + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }
    
    public void adicionarBytes(String msg, byte[] bytes) {
    StringBuilder sb = new StringBuilder(msg + ": ");
    for (byte b : bytes) {
        sb.append(String.format("%02X ", b));
    }
    areaLog.append(sb.toString().trim() + "\n");
    areaLog.setCaretPosition(areaLog.getDocument().getLength());
}

    public void limpar() {
        areaLog.setText("");
        adicionarMensagem("Log limpo.");
    }
}