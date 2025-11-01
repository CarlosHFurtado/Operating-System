package interfacesicxe;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.TitledBorder;

public class PainelRegistradores extends JPanel {

    private static final Color COR_FUNDO_PAINEL = new Color(63, 84, 114);
    private static final Color COR_DETALHE = new Color(6,8,11);
    private static final Color COR_TEXTO = Color.WHITE;
    private static final Color COR_CAMPO = new Color(63, 84, 114); // Mesmo fundo!

    private JTextField txtA, txtX, txtL, txtPC, txtSW;

    public PainelRegistradores() {
        setLayout(new GridLayout(5, 2, 8, 12));
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COR_DETALHE, 1),
            "Registradores",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            COR_DETALHE
        ));
        setBackground(COR_FUNDO_PAINEL);
        setOpaque(true);

        criarRegistro("A:", "00FF");
        criarRegistro("X:", "1000");
        criarRegistro("L:", "0000");
        criarRegistro("PC:", "0020");
        criarRegistro("SW:", "0000");
    }

    private void criarRegistro(String nome, String valor) {
        JLabel label = new JLabel(nome);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(COR_DETALHE); // Rótulo em rosa

        JTextField campo = new JTextField(valor);
        campo.setFont(new Font("Consolas", Font.BOLD, 14));
        campo.setHorizontalAlignment(JTextField.CENTER);
        campo.setEditable(false);
        campo.setBackground(COR_CAMPO); // ← Mesmo fundo que o painel!
        campo.setForeground(COR_TEXTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 120, 140), 1),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));

        add(label);
        add(campo);

        switch (nome) {
            case "A:" -> txtA = campo;
            case "X:" -> txtX = campo;
            case "L:" -> txtL = campo;
            case "PC:" -> txtPC = campo;
            case "SW:" -> txtSW = campo;
        }
    }

    public void resetar() {
        txtA.setText("0000");
        txtX.setText("0000");
        txtL.setText("0000");
        txtPC.setText("0000");
        txtSW.setText("0000");
    }
}