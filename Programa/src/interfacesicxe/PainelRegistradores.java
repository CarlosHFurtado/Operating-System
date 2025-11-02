package interfacesicxe;

import Executor.Executor;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.TitledBorder;

public class PainelRegistradores extends JPanel {

    private static final Color COR_FUNDO_PAINEL = new Color(63, 84, 114);
    private static final Color COR_DETALHE = new Color(6, 8, 11);
    private static final Color COR_TEXTO = Color.WHITE;
    private static final Color COR_CAMPO = new Color(63, 84, 114);

    private Executor executor;
    private JTextField txtA, txtX, txtL, txtB, txtS, txtT, txtPC, txtSW;
    private boolean modoEdicao = false;

    public PainelRegistradores(Executor executor) {
        this.executor = executor;
        setLayout(new GridLayout(8, 2, 8, 12)); // 8 registradores
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

        criarRegistro("A:", txtA = new JTextField());
        criarRegistro("X:", txtX = new JTextField());
        criarRegistro("L:", txtL = new JTextField());
        criarRegistro("B:", txtB = new JTextField());
        criarRegistro("S:", txtS = new JTextField());
        criarRegistro("T:", txtT = new JTextField());
        criarRegistro("PC:", txtPC = new JTextField());
        criarRegistro("SW:", txtSW = new JTextField());

        atualizar();
    }

    private void criarRegistro(String nome, JTextField campo) {
        JLabel label = new JLabel(nome);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(COR_DETALHE);

        campo.setFont(new Font("Consolas", Font.BOLD, 14));
        campo.setHorizontalAlignment(JTextField.CENTER);
        campo.setEditable(modoEdicao);
        campo.setBackground(COR_CAMPO);
        campo.setForeground(COR_TEXTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 120, 140), 1),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));

        campo.addActionListener(e -> {
            try {
                String texto = campo.getText().trim();
                int valor = Integer.parseUnsignedInt(texto, 16);
                switch (nome) {
                    case "A:" -> executor.getRegistradores().setValor("A", valor);
                    case "X:" -> executor.getRegistradores().setValor("X", valor);
                    case "L:" -> executor.getRegistradores().setValor("L", valor);
                    case "B:" -> executor.getRegistradores().setValor("B", valor);
                    case "S:" -> executor.getRegistradores().setValor("S", valor);
                    case "T:" -> executor.getRegistradores().setValor("T", valor);
                    case "PC:" -> executor.getRegistradores().setValor("PC", valor);
                    case "SW:" -> executor.getRegistradores().setValor("SW", valor);
                }
                JOptionPane.showMessageDialog(PainelRegistradores.this, "Registrador '" + nome + "' atualizado.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(PainelRegistradores.this, 
                    "Valor inválido. Use hexadecimal (ex: 0000FF).", 
                    "Erro", JOptionPane.ERROR_MESSAGE);
                atualizar();
            }
        });

        add(label);
        add(campo);
    }

    public void atualizar() {
        var regs = executor.getRegistradores();

        txtA.setText(String.format("%06X", regs.getValor("A") & 0xFFFFFF));
        txtX.setText(String.format("%06X", regs.getValor("X") & 0xFFFFFF));
        txtL.setText(String.format("%06X", regs.getValor("L") & 0xFFFFFF));
        txtB.setText(String.format("%06X", regs.getValor("B") & 0xFFFFFF));
        txtS.setText(String.format("%06X", regs.getValor("S") & 0xFFFFFF));
        txtT.setText(String.format("%06X", regs.getValor("T") & 0xFFFFFF));
        txtPC.setText(String.format("%06X", regs.getValor("PC") & 0xFFFFFF));
        txtSW.setText(String.format("%04X", regs.getValor("SW") & 0xFFFF));

        txtA.setEditable(modoEdicao);
        txtX.setEditable(modoEdicao);
        txtL.setEditable(modoEdicao);
        txtB.setEditable(modoEdicao);
        txtS.setEditable(modoEdicao);
        txtT.setEditable(modoEdicao);
        txtPC.setEditable(modoEdicao);
        txtSW.setEditable(modoEdicao);
    }

    public void resetar() {
        executor.limpar();
        atualizar();
    }

    public void alternarModoEdicao() {
        modoEdicao = !modoEdicao;
        atualizar();
    }

    public boolean isModoEdicao() {
        return modoEdicao;
    }
}